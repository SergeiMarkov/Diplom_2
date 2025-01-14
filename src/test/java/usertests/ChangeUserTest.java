package usertests;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import url.BaseURL;
import user.User;
import user.UserGenerator;
import user.UserStep;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class ChangeUserTest {

    private final static String UPDATE_EMAIL = UserGenerator.generateUserEmail();
    private final static String UPDATE_NAME = UserGenerator.generateUserName();
    BaseURL baseURL = new BaseURL();
    UserStep userStep = new UserStep();
    User user = new User(
            UserGenerator.generateUserEmail(),
            UserGenerator.generateUserName(),
            UserGenerator.generateUserPassword());
    private String accessToken;

    @Before
    public void setUp() {
        baseURL.setUp();
        UserStep.creatingUser(user);
        accessToken = userStep.loginUser(user).then().extract().path("accessToken");
    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            userStep.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Возможно сменить email авторизованному пользователю")
    @Description("Позитивная проверка смены email авторизованным пользователем, возвращение корректного статуса и тела ответа")
    public void changeAuthUserEmailTest() {
        user.setEmail(UPDATE_EMAIL);
        userStep.patchUser(user, accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()));
    }

    @Test
    @DisplayName("Не возможно сменить email неавторизованному пользователю")
    @Description("Негативная проверка смены email неавторизованным пользователем, возвращение корректного статуса и тела ответа")
    public void changeNotAuthUserEmailTest() {
        accessToken = "";
        user.setEmail(UPDATE_EMAIL);
        userStep.patchUser(user, accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Возможно сменить name авторизованному пользователю")
    @Description("Позитивная проверка смены name авторизованным пользователем, возвращение корректного статуса и тела ответа")
    public void changeAuthUserNameTest() {
        user.setName(UPDATE_NAME);
        userStep.patchUser(user, accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Не возможно сменить name неавторизованному пользователю")
    @Description("Негативная проверка смены name неавторизованным пользователем, возвращение корректного статуса и тела ответа")
    public void changeNotAuthUserNameTest() {
        accessToken = "";
        user.setName(UPDATE_NAME);
        userStep.patchUser(user, accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Не возможно сменить email, который уже используется")
    @Description("Негативная проверка смены email, который уже используется, возвращение корректного статуса и тела ответа")
    public void changeAuthUserWithExistEmailTest() {
        String usedEmail = user.getEmail();
        User anotherUser = new User(
                UserGenerator.generateUserEmail(),
                UserGenerator.generateUserName(),
                UserGenerator.generateUserPassword());
        String newName = anotherUser.getName();
        String newPassword = anotherUser.getPassword();
        String newAccessToken = UserStep.creatingUser(anotherUser).jsonPath().getString("accessToken");
        User patchAnotherUser = new User(usedEmail, newName, newPassword);
        userStep.patchUser(patchAnotherUser, newAccessToken)
                .then()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));

        userStep.deleteUser(newAccessToken);
    }

}
