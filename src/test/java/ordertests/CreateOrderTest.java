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

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderTest {

    private final static String INGREDIENT_WITH_WRONG_HASH = "ingredientwithwronghash";
    BaseURL baseURL = new BaseURL();
    UserStep userStep = new UserStep();
    OrderStep orderStep = new OrderStep();
    IngredientStep ingredientStep = new IngredientStep();
    User user = new User(
            UserGenerator.generateUserEmail(),
            UserGenerator.generateUserName(),
            UserGenerator.generateUserPassword());
    private String accessToken;
    private List<String> ingredients;
    private Order order;

    @Before
    public void setUp() {

        baseURL.setUp();
        Ingredient ingredientList = ingredientStep.getIngredient();
        ingredients = new ArrayList<>();
        ingredients.add(ingredientList.getData().get(1).getId());
        ingredients.add(ingredientList.getData().get(2).getId());
        ingredients.add(ingredientList.getData().get(3).getId());
        UserStep.creatingUser(user);
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
    @DisplayName("Возможно создать заказ авторизованному пользователю")
    @Description("Позитивная проверка создания заказа авторизованным пользователем, возвращение корректного статуса и тела ответа")
    public void createOrderAuthUserTest() {
        orderStep.createOrderAuthUser(order, accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Возможно создать заказ неавторизованному пользователю")
    @Description("Позитивная проверка создания заказа неавторизованным пользователем, возвращение корректного статуса и тела ответа")
    public void createOrderNotAuthUserTest() {
        orderStep.createOrderNotAuthUser(order)
                .then().log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("order", notNullValue());
    }

    @Test
    @DisplayName("Не возможно создать заказ без передачи ингредиента вторизованному пользователю")
    @Description("Негативная проверка создания заказа без передачи ингредиента авторизованным пользователем, возвращение корректного статуса и тела ответа")
    public void createOrderNoIngredientsTest() {
        ingredients.clear();
        order = new Order(ingredients);
        orderStep.createOrderAuthUser(order, accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Не возможно создать заказ, если в запросе передан невалидный хеш ингредиента, авторизованному пользователю")
    @Description("Негативная проверка создания заказа, если в запросе передан невалидный хеш ингредиента, пользователем, возвращение корректного статуса и тела ответа")
    public void createOrderWithWrongHashTest() {
        ingredients.clear();
        ingredients.add(INGREDIENT_WITH_WRONG_HASH);
        order = new Order(ingredients);
        orderStep.createOrderAuthUser(order, accessToken)
                .then().log().all()
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

}
