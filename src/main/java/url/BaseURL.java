package url;

import io.restassured.RestAssured;

public class BaseURL {
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

}
