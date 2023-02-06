package ScooterApiTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrdersListGetTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }

    @Test
    public void checkThatOrdersListHasData() {
        Response response = given().get("/api/v1/orders");
        response.then().assertThat().body(notNullValue());
    }
}