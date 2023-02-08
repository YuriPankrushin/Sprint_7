package ScooterApiTest;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.CourierData;
import org.example.OrderData;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class OrdersListGetTest {
    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Получить список заказов без указания параметров фильтрации")
    @Description("Проверить, что метод возвращает не пустой список заказов, если не указываются параметры фильтрации")
    public void checkThatOrdersListWithoutParamsHasData() {
        Response response = given().get("/api/v1/orders");
        response.then().assertThat().body(notNullValue());
    }

    @Test
    @DisplayName("Получить список заказов курьера")
    @Description("Проверить, что метод, с указанием идентификатора курьера, возвращает список из трех заказов")
    public void checkThatOrderListByCourierHasThreeItems() {

        /** Test data setup */

        //создать курьера
        CourierData courierData = new CourierData("mrorange", "12345", "Мистер Оранжевый");
        Response postNewCourier =
                given()
                        .header("Content-type", "application/json")
                        .body(courierData)
                        .when()
                        .post("/api/v1/courier");

        //авторизоваться курьером
        CourierData courierLoginData = new CourierData("mrorange", "12345");

        Response courierLogin =
                given()
                        .header("Content-type", "application/json")
                        .body(courierLoginData)
                        .when()
                        .post("/api/v1/courier/login");

        //получить id курьера
        int courierId = courierLogin.then().extract().path("id");

        //создать заказ 1
        OrderData order1 = new OrderData("Иван", "Иванов", "Москва, 2-я Пугачёвская улица, 10к2", "62", "79267777711", 7, "2023-02-15", "Хочу кататься", List.of("GREY"));
        Response newOrder1 =
                given()
                        .header("Content-type", "application/json")
                        .body(order1)
                        .when()
                        .post("/api/v1/orders");

        //получить track номер заказа
        int track1Number = newOrder1.then().extract().path("track");

        //Получить заказ по его номеру, чтобы потом получить id того же заказа
        Response getOrder1ById =
                given()
                        .header("Content-type", "application/json")
                        .when()
                        .get(String.format("/api/v1/orders/track?t=%s", track1Number));
        int order1Id = getOrder1ById.then().extract().path("order.id");

        //создать заказ 2
        OrderData order2 = new OrderData("Юрий", "Панкрушин", "Москва, Ярославская улица, 13А", "96", "79267777722", 5, "2023-02-20", "Вжух!", List.of("BLACK"));
        Response newOrder2 =
                given()
                        .header("Content-type", "application/json")
                        .body(order2)
                        .when()
                        .post("/api/v1/orders");

        //получить track номер заказа
        int track2Number = newOrder2.then().extract().path("track");

        //Получить заказ по его номеру, чтобы потом получить id того же заказа
        Response getOrder2ById =
                given()
                        .header("Content-type", "application/json")
                        .when()
                        .get(String.format("/api/v1/orders/track?t=%s", track2Number));
        int order2Id = getOrder2ById.then().extract().path("order.id");

        //создать заказ 3
        OrderData order3 = new OrderData("Юрий", "Панкрушин", "Москва, 8-я улица Соколиной Горы, 24к1", "215", "79267777755", 3, "2023-02-09", "Сдачу оставь себе", List.of("GREY", "BLACK"));
        Response newOrder3 =
                given()
                        .header("Content-type", "application/json")
                        .body(order3)
                        .when()
                        .post("/api/v1/orders");

        //получить track номер заказа
        int track3Number = newOrder3.then().extract().path("track");

        //Получить заказ по его номеру, чтобы потом получить id того же заказа
        Response getOrder3ById =
                given()
                        .header("Content-type", "application/json")
                        .when()
                        .get(String.format("/api/v1/orders/track?t=%s", track3Number));

        int order3Id = getOrder3ById.then().extract().path("order.id");

        //принять заказ 1
        Response response1 = given()
                .header("Content-type", "application/json")
                .when()
                .put(String.format("/api/v1/orders/accept/%s?courierId=%s", order1Id, courierId));

        //принять заказ 2
        Response response2 = given()
                .header("Content-type", "application/json")
                .when()
                .put(String.format("/api/v1/orders/accept/%s?courierId=%s", order2Id, courierId));

        //принять заказ 3
        Response response3 = given()
                .header("Content-type", "application/json")
                .when()
                .put(String.format("/api/v1/orders/accept/%s?courierId=%s", order3Id, courierId));


        /** Test */
        Response response = given().queryParam("courierId", courierId).get("/api/v1/orders");
        response.then().assertThat()
                .statusCode(SC_OK)
                .and()
                .body("pageInfo.total", equalTo(3));

        /** Clear test data */
        //Delete courier
        given().header("Content-type", "application/json").
                pathParam(":id", String.valueOf(courierId)).when().delete("/api/v1/courier/{:id}");


        /** Комментарии к тесту */
        //Тест показывет, что метод /api/v1/orders/accept дублирует один заказ, поэтому итоговое количество заказов у курьера больше вдое
        //Из-за того, что тест фейлится, не доходит до очистки данных
    }
}