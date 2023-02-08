package scooterapitest;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.CourierData;
import org.example.OrderData;
import org.junit.Test;

import java.util.List;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrdersListGetTest extends BaseTest {

    @Test
    @DisplayName("Получить список заказов без указания параметров фильтрации")
    @Description("Проверить, что метод возвращает не пустой список заказов, если не указываются параметры фильтрации")
    public void checkThatOrdersListWithoutParamsHasData() {
        Response allOrders = orderApi.getAllOrders();
        allOrders.then().assertThat().body(notNullValue());
    }

    @Test
    @DisplayName("Получить список заказов курьера")
    @Description("Проверить, что метод, с указанием идентификатора курьера, возвращает список из трех заказов")
    public void checkThatOrderListByCourierHasThreeItems() {

        /** Test data setup */

        //Создать курьера
        CourierData courierData = new CourierData("mrorange", "12345", "Мистер Оранжевый");
        courierApi.courierCreate(courierData);

        //Авторизоваться курьером
        CourierData courierLoginData = new CourierData("mrorange", "12345");
        courierApi.courierLogin(courierLoginData);

        //Получить id курьера
        int courierId = courierApi.getCourierId(courierLoginData);

        //Создать три заказа
        OrderData order1 = new OrderData("Иван", "Иванов", "Москва, 2-я Пугачёвская улица, 10к2", "62", "79267777711", 7, "2023-02-15", "Хочу кататься", List.of("GREY"));
        OrderData order2 = new OrderData("Федор", "Федоров", "Москва, Ярославская улица, 13А", "96", "79267777722", 5, "2023-02-20", "Вжух!", List.of("BLACK"));
        OrderData order3 = new OrderData("Сергей", "Сергеев", "Москва, 8-я улица Соколиной Горы, 24к1", "215", "79267777755", 3, "2023-02-09", "Сдачу оставь себе", List.of("GREY", "BLACK"));

        //Принять заказы курьером
        orderApi.orderAcceptByCourier(order1, courierId);
        orderApi.orderAcceptByCourier(order2, courierId);
        orderApi.orderAcceptByCourier(order3, courierId);


        /** Test */
        //Проверить, что у курьера три заказа
        Response response = orderApi.getCourierOrders(courierId);
        response.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("pageInfo.total", equalTo(3));

        /** Clear test data */
        //Delete courier
        courierApi.courierDelete(courierLoginData);
    }
}