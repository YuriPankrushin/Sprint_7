package scooterapitest;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.CourierData;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_CONFLICT;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.equalTo;

public class CourierCreatePostTest extends BaseTest {

    @Test
    @DisplayName("Успешное создание курьера")
    @Description("Успешно создать курьера с указанием логина, пароля и имени пользователя")
    public void checkThatCourierCouldBeCreated() {
        //Данные курьера
        CourierData courierData = new CourierData("yuri", "12345", "Юрий");
        //Создать курьера
        Response postNewCourier = courierApi.courierCreate(courierData);
        //Проверить, что вернулся правильный ответ и статус код
        postNewCourier.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(SC_CREATED);

        /** Clean test data **/
        CourierData courierLoginData = new CourierData("yuri", "12345");
        courierApi.courierLogin(courierLoginData);
        courierApi.courierDelete(courierLoginData);
    }

    @Test
    @DisplayName("Создание курьера без имени")
    @Description("Проверить неудачное создание курьера без указания логина")
    public void checkThatCourierCouldNotBeCreatedWithoutLogin() {
        //Данные курьера
        CourierData courierData = new CourierData(null, "12345", "Владимир");
        //Создать курьера
        Response postNewCourier = courierApi.courierCreate(courierData);
        //Проверить, что вернулся правильный ответ и статус код
        postNewCourier.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    @Description("Проверить неудачное создание курьера без указания пароля")
    public void checkThatCourierCouldNotBeCreatedWithoutPassword() {
        //Данные курьера
        CourierData courierData = new CourierData("jora", null, "Георгий");
        //Создать курьера
        Response postNewCourier = courierApi.courierCreate(courierData);
        //Проверить, что вернулся правильный ответ и статус код
        postNewCourier.then().assertThat().body("message", equalTo("Недостаточно данных для создания учетной записи"))
                .and()
                .statusCode(SC_BAD_REQUEST);
    }

    @Test
    @DisplayName("Создание нового курьера с данными имеющегося курьера")
    @Description("Проверить, что нельзя создать двух курьеров с одинаковыми входными данными")
    public void checkThatImpossibleToCreateTwoCouriersWithSameCredentials() {
        //Данные курьера
        CourierData courierData = new CourierData("yuri", "12345", "Юрий");
        //Создать курьера
        Response postNewCourier = courierApi.courierCreate(courierData);
        //Проверить, что вернулся правильный ответ и статус код
        postNewCourier.then().assertThat().body("ok", equalTo(true))
                .and()
                .statusCode(SC_CREATED);

        //Создать курьера с данными первого курьера
        Response postNewCourierWithSameCreds = courierApi.courierCreate(courierData);
        //Проверить, что вернулся правильный ответ и статус код
        postNewCourierWithSameCreds.then().assertThat().body("message", equalTo("Этот логин уже используется. Попробуйте другой."))
                .and()
                .statusCode(SC_CONFLICT);

        /** Clean test data **/
        CourierData courierLoginData = new CourierData("yuri", "12345");
        courierApi.courierLogin(courierLoginData);
        courierApi.courierDelete(courierLoginData);
    }
}