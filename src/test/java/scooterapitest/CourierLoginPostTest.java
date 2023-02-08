package scooterapitest;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.CourierData;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

public class CourierLoginPostTest extends BaseTest {

    @Test
    @DisplayName("Успешная авторизация")
    @Description("Успешно авторизоваться под существующим курьером")
    public void checkThatCourierCouldLogInToTheApp() {
        //Данные курьера
        CourierData courierData = new CourierData("jora", "12345", "Жора");
        //Создать курьера
        courierApi.courierCreate(courierData);

        //Авторизация курьера с существующими данными
        CourierData courierLoginData = new CourierData("jora", "12345");
        Response courierLogin = courierApi.courierLogin(courierLoginData);
        //Проверить, что ответ содержит поле id и правильный статус код
        courierLogin.then().assertThat().body("$", hasKey("id"))
                .and()
                .statusCode(SC_OK);

        /** Clear test data */
        //Extract courier id value from courierLogin response body
//        int courierId = courierLogin.then().extract().path("id");
//        //Delete courier
//        given(baseApi.requestSpecification).header("Content-type", "application/json").
//                pathParam(":id", String.valueOf(courierId)).when().delete("/api/v1/courier/{:id}");
        courierApi.courierDelete(courierLoginData);

    }

    @Test
    @DisplayName("Авторизация с несуществующими логином и паролем")
    @Description("Проверить, что авторизация не пройдет, если данные для входа неверные")
    public void checkThatCourierCouldNotLoginWithIncorrectCredentials() {
        //Авторизация курьера с несуществующими данными
        CourierData courierLoginData = new CourierData("yurivovajora", "gdvajs12!@");
        Response courierLogin = courierApi.courierLogin(courierLoginData);
        //Проверить, что вернулся правильный ответ и статус код
        courierLogin.then().assertThat().body("message", equalTo("Учетная запись не найдена"))
                .and()
                .statusCode(SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Авторизация без пароля")
    @Description("Проверить, что авторизация не пройдет, если пароль не передан")
    public void checkThatCourierCouldNotLoginWithoutPassword() {
        //Авторизация курьера без указания пароля
        CourierData courierLoginData = new CourierData("yurivovajora", null);
        Response courierLogin = courierApi.courierLogin(courierLoginData);
        //Проверить, что вернулся правильный ответ и статус код
        courierLogin.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Авторизация без логина")
    @Description("Проверить, что авторизация не пройдет, если логин не передан")
    public void checkThatCourierCouldNotLoginWithoutLogin() {
        //Авторизация курьера без указания логина
        CourierData courierLoginData = new CourierData(null, "lasn!12a");
        Response courierLogin = courierApi.courierLogin(courierLoginData);
        //Проверить, что вернулся правильный ответ и статус код
        courierLogin.then().assertThat().body("message", equalTo("Недостаточно данных для входа"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }
}