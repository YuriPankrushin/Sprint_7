package scooterapitest;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.CourierData;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

public class CourierLoginPostTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Успешная авторизация")
    @Description("Успешно авторизоваться под существующим курьером")
    public void checkThatCourierCouldLogInToTheApp() {
        //Courier data
        CourierData courierData = new CourierData("jora", "12345", "Жора");

        //Create courier request
        given().header("Content-type", "application/json").body(courierData).when().post("/api/v1/courier");

        //Login with correct credentials
        CourierData courierLoginData = new CourierData("jora", "12345");

        Response courierLogin =
                given()
                        .header("Content-type", "application/json")
                        .body(courierLoginData)
                        .when()
                        .post("/api/v1/courier/login");

        //Check that courier id was given and has response has correct code
        courierLogin.then().assertThat().body("$", hasKey("id"))
                .and()
                .statusCode(SC_OK);

        /** Clear test data */
        //Extract courier id value from courierLogin response body
        int courierId = courierLogin.then().extract().path("id");
        //Delete courier
        given().header("Content-type", "application/json").
                pathParam(":id", String.valueOf(courierId)).when().delete("/api/v1/courier/{:id}");
    }

    @Test
    @DisplayName("Авторизация с несуществующими логином и паролем")
    @Description("Проверить, что авторизация не пройдет, если данные для входа неверные")
    public void checkThatCourierCouldNotLoginWithIncorrectCredentials() {
        //Login with incorrect credentials
        CourierData courierLoginData = new CourierData("yurivovajora", "gdvajs12!@");

        Response courierLogin =
                given()
                        .header("Content-type", "application/json")
                        .body(courierLoginData)
                        .when()
                        .post("/api/v1/courier/login");

        //Check that login failed and has correct response code
        courierLogin.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Авторизация без пароля")
    @Description("Проверить, что авторизация не пройдет, если пароль не передан")
    public void checkThatCourierCouldNotLoginWithoutPassword() {
        //Login without password
        CourierData courierLoginData = new CourierData("yurivovajora", null);

        Response courierLogin =
                given()
                        .header("Content-type", "application/json")
                        .body(courierLoginData)
                        .when()
                        .post("/api/v1/courier/login");

        //Check that login failed and has correct response code
        courierLogin.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Авторизация без логина")
    @Description("Проверить, что авторизация не пройдет, если логин не передан")
    public void checkThatCourierCouldNotLoginWithoutLogin() {
        //Login without login
        CourierData courierLoginData = new CourierData(null, "lasn!12a");

        Response courierLogin =
                given()
                        .header("Content-type", "application/json")
                        .body(courierLoginData)
                        .when()
                        .post("/api/v1/courier/login");

        //Check that login failed and has correct response code
        courierLogin.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }
}