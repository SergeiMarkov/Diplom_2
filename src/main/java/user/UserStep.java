package user;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserStep {

    private static final String CREATE_USER_PATH = "api/auth/register";
    private static final String LOGIN_USER_PATH = "api/auth/login";
    private static final String PATCH_USER_PATH = "api/auth/user";
    private static final String DELETE_USER_PATH = "api/auth/user";

    @Step("Создания пользователя")
    public static Response creatingUser(User user) {
        return given().log().all()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(CREATE_USER_PATH);

    }

    @Step("Логин пользователя")
    public Response loginUser(User user) {
        return given().log().all()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(LOGIN_USER_PATH);

    }

    @Step("Изменение данных пользователя")
    public Response patchUser(User user, String accessToken) {
        return given().log().all()
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .body(user)
                .patch(PATCH_USER_PATH);
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return given().log().all()
                .header("Authorization", accessToken)
                .when()
                .delete(DELETE_USER_PATH);
    }
}
