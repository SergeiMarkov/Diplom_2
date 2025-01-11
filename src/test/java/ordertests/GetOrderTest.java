package ordertests;

import ingredients.Ingredient;
import ingredients.IngredientStep;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import order.Order;
import order.OrderStep;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import url.BaseURL;
import user.User;
import user.UserGenerator;
import user.UserStep;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.*;

public class GetOrderTest {

    BaseURL baseURL = new BaseURL();
    UserStep userStep = new UserStep();
    OrderStep orderStep = new OrderStep();
    private String accessToken;
    private Ingredient ingredientList;
    private List<String> ingredients;
    private Order order;
    IngredientStep ingredientStep = new IngredientStep();
    User user = new User(
            UserGenerator.generateUserEmail(),
            UserGenerator.generateUserName(),
            UserGenerator.generateUserPassword());

    @Before
    public void setUp() {
        baseURL.setUp();
        ingredientList = ingredientStep.getIngredient();
        ingredients = new ArrayList<>();
        ingredients.add(ingredientList.getData().get(1).get_id());
        ingredients.add(ingredientList.getData().get(2).get_id());
        ingredients.add(ingredientList.getData().get(3).get_id());
        userStep.creatingUser(user);
        accessToken = userStep.loginUser(user).then().extract().path("accessToken");
        order = new Order(ingredients);
    }

    @After
    public void deleteUser() {
        if (accessToken != null) {
            userStep.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Возможно получить заказ авторизованному пользователю")
    @Description("Позитивная проверка получения заказа авторизованным пользователем, возвращение корректного статуса и тела ответа")
    public void getOrderAuthUserTest() {
        orderStep.getOrdersAuthUser(accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("orders", notNullValue());
    }

    @Test
    @DisplayName("Check get order not auth user")
    public void getOrderNotAuthUserTest() {
        orderStep.getOrdersNotAuthUser()
                .then().log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

}
