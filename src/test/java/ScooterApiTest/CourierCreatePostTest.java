package ScooterApiTest;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.CourierData;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.equalTo;

public class CourierCreatePostTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru";
    }

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Успешно создать курьера с указанием логина, пароля и имени пользователя")
    public void checkThatCourierCouldBeCreated() {
        //Courier data
        CourierData courierData = new CourierData("yuri", "12345", "Юрий");

        //Send create courier request
        Response postNewCourier =
                given()
                        .header("Content-type", "application/json")
                        .body(courierData)
                        .when()
                        .post("/api/v1/courier");

        //Check successful and correct response
        postNewCourier.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(SC_CREATED);

        /** Clean test data **/
        //Login to take courier id
        CourierData courierLoginData = new CourierData("yuri", "12345");

        Response courierLogin =
                given().header("Content-type", "application/json").body(courierLoginData).when().post("/api/v1/courier/login");

        //Extract courier id value from courierLogin response body
        int courierId = courierLogin.then().extract().path("id");
        //Delete courier
        given().header("Content-type", "application/json").
                pathParam(":id", String.valueOf(courierId)).when().delete("/api/v1/courier/{:id}");
    }

    @Test
    @DisplayName("Создание курьера без имени")
    @Description("Проверить неудачное создание курьера без указания логина")
    public void checkThatCourierCouldNotBeCreatedWithoutLogin() {
        //Courier data
        CourierData courierData = new CourierData(null, "12345", "Владимир");

        //Send create courier request
        Response postNewCourier =
                given()
                        .header("Content-type", "application/json")
                        .body(courierData)
                        .when()
                        .post("/api/v1/courier");

        //Check fail message and correct response code
        postNewCourier.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    @Description("Проверить неудачное создание курьера без указания пароля")
    public void checkThatCourierCouldNotBeCreatedWithoutPassword() {
        //Courier data
        CourierData courierData = new CourierData("jora", null, "Георгий");

        //Send create courier request
        Response postNewCourier =
                given()
                        .header("Content-type", "application/json")
                        .body(courierData)
                        .when()
                        .post("/api/v1/courier");

        //Check fail message and correct response code
        postNewCourier.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Создание нового курьера с данными имеющегося курьера")
    @Description("Проверить, что нельзя создать двух курьеров с одинаковыми входными данными")
    public void checkThatImpossibleToCreateTwoCouriersWithSameCredentials() {
        //Courier data
        CourierData courierData = new CourierData("yuri", "12345", "Юрий");

        //Send create courier request
        Response postNewCourier =
                given()
                        .header("Content-type", "application/json")
                        .body(courierData)
                        .when()
                        .post("/api/v1/courier");

        //Check successful and correct response
        postNewCourier.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(SC_CREATED);

        //Send second create courier request with the same courier credentials as before
        Response postNewCourierWithSameCreds =
                given()
                        .header("Content-type", "application/json")
                        .body(courierData)
                        .when()
                        .post("/api/v1/courier");

        //Check fail message and correct response code
        postNewCourierWithSameCreds.then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .and()
                .statusCode(SC_CONFLICT);

        /** Clean test data **/
        //Login to take courier id
        CourierData courierLoginData = new CourierData("yuri", "12345");

        Response courierLogin =
                given().header("Content-type", "application/json").body(courierLoginData).when().post("/api/v1/courier/login");

        //Extract courier id value from courierLogin response body
        int courierId = courierLogin.then().extract().path("id");
        //Delete courier
        given().header("Content-type", "application/json").
                pathParam(":id", String.valueOf(courierId)).when().delete("/api/v1/courier/{:id}");
    }
}