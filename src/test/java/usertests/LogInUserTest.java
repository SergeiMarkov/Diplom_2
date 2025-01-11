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
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;


public class LogInUserTest {

    BaseURL baseURL = new BaseURL();
    UserStep userStep = new UserStep();
    private String accessToken;
    User user = new User(
            UserGenerator.generateUserEmail(),
            UserGenerator.generateUserName(),
            UserGenerator.generateUserPassword());

    private final static String INCORRECT_EMAIL = UserGenerator.generateUserEmail();
    private final static String INCORRECT_PASSWORD = UserGenerator.generateUserPassword();

    @Before
    public void setUp() {
        baseURL.setUp();
        userStep.creatingUser(user);
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
                .body("success", equalTo(true));
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
