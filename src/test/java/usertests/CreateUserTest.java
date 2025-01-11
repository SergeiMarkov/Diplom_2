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
import static org.hamcrest.Matchers.equalTo;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;


public class CreateUserTest {

    BaseURL baseURL = new BaseURL();
    UserStep userStep = new UserStep();
    private String accessToken;
    User user = new User(
            UserGenerator.generateUserEmail(),
            UserGenerator.generateUserName(),
            UserGenerator.generateUserPassword());

    @Before
    public void setUp() {
        baseURL.setUp();
    }

    @After
    public void deleteUser() {
        accessToken = userStep.loginUser(user).then().extract().path("accessToken");
        if (accessToken != null) {
            userStep.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешное создание пользователя")
    @Description("Позитивная проверка создания пользователя, возвращение корректного статуса и тела ответа")
    public void creatingUserTest() {
        userStep.creatingUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Не возможно создать пользователя дважды")
    @Description("Негативная проверка создания второго иденитичного пользователя, возвращение корректного статуса и тела ответа")
    public void creatingAUserTwiceTest() {
        userStep.creatingUser(user);
        userStep.creatingUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Не возможно создать пользователя без пароля")
    @Description("Негативная проверка создания пользователя с пустым паролем, возвращение корректного статуса и тела ответа")
    public void creatingAUserWithoutAPasswordTest() {
        user.setPassword("");
        userStep.creatingUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Не возможно создать пользователя без email")
    @Description("Негативная проверка создания пользователя с пустым email, возвращение корректного статуса и тела ответа")
    public void creatingAUserWithoutAEmailTest() {
        user.setEmail("");
        userStep.creatingUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Не возможно создать пользователя без name")
    @Description("Негативная проверка создания пользователя с пустым name, возвращение корректного статуса и тела ответа")
    public void creatingAnUnnamedUserTest() {
        user.setName("");
        userStep.creatingUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

}
