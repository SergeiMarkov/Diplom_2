package ingredients;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import order.Order;

import static io.restassured.RestAssured.given;

public class IngredientStep {

    private static final String INGREDIENTS_PATH = "api/ingredients";

    @Step("Получение информации об ингредиентах")
    public Ingredient getIngredient() {
        return given().log().all()
                .header("Content-type", "application/json")
                .get(INGREDIENTS_PATH)
                .as(Ingredient.class);

    }

}
