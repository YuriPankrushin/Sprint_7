package ScooterApiTest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.CourierData;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CourierCreatePostTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Test
    public void createNewCourier() {
        CourierData courierData = new CourierData("yuri", "12345", "Юрий");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .body(courierData)
                        .when()
                        .post("/api/v1/courier");
        response.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(201);
    }
}
