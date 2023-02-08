package scooterapitest;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.CourierData;
import org.junit.AfterClass;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

public class CourierLoginPostTest extends BaseTest {

    /** Тестовые данные */
    //Создание курьера
    static CourierData courierJoraData = new CourierData("jora991", "12345", "Жора");
    //Данные для авторизации курьера
    static CourierData courierJoraLoginData = new CourierData("jora991", "12345");

    @AfterClass
    public static void testDataClear(){
        /** Удаление тестовых данных */
        //Удаление курьера
        courierApi.courierDelete(courierJoraLoginData);
    }

    @Test
    @DisplayName("Успешная авторизация")
    @Description("Успешно авторизоваться под существующим курьером")
    public void checkThatCourierCouldLogInToTheApp() {
        //Создать курьера
        courierApi.courierCreate(courierJoraData);
        //Авторизация курьера с существующими данными
        Response courierLogin = courierApi.courierLogin(courierJoraLoginData);
        //Проверить, что ответ содержит поле id и правильный статус код
        courierLogin.then().assertThat().body("$", hasKey("id"))
                .and()
                .statusCode(SC_OK);
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