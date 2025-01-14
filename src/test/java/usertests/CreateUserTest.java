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

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;


public class CreateUserTest {

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
        UserStep.creatingUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Не возможно создать пользователя дважды")
    @Description("Негативная проверка создания второго иденитичного пользователя, возвращение корректного статуса и тела ответа")
    public void creatingAUserTwiceTest() {
        UserStep.creatingUser(user);
        UserStep.creatingUser(user)
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
        UserStep.creatingUser(user)
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
        UserStep.creatingUser(user)
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
        UserStep.creatingUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

}
