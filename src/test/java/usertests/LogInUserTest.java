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

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;


public class LogInUserTest {

    private final static String INCORRECT_EMAIL = UserGenerator.generateUserEmail();
    private final static String INCORRECT_PASSWORD = UserGenerator.generateUserPassword();
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
        accessToken = userStep.loginUser(user)
                .then()
                .extract()
                .path("accessToken");
    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            userStep.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Возможно авторизоваться (логин) пользователю")
    @Description("Позитивная проверка возможности авторизации пользователя, возвращение корректного статуса и тела ответа")
    public void authorizationUserTest() {
        userStep.loginUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Не возможно авторизоваться (логин) пользователю с некорректным email")
    @Description("Негативная проверка, пользовательне может авторизоваться с некорректным email, возвращение корректного статуса и тела ответа")
    public void authorizationWithIncorrectEmailTest() {
        user.setEmail(INCORRECT_EMAIL);
        userStep.loginUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Не возможно авторизоваться (логин) пользователю с некорректным password")
    @Description("Негативная проверка, пользовательне может авторизоваться с некорректным password, возвращение корректного статуса и тела ответа")
    public void authorizationWithIncorrectPasswordTest() {
        user.setPassword(INCORRECT_PASSWORD);
        userStep.loginUser(user)
                .then().log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

}
