package org.example.utils;

import io.restassured.response.Response;
import org.example.OrderData;

import static io.restassured.RestAssured.given;

public class OrderApi extends BaseApi {

    //Создать заказ
    public Response orderCreate(OrderData orderData) {
        return given(requestSpecification)
                .header("Content-type", "application/json")
                .body(orderData)
                .when()
                .post("/api/v1/orders");
    }

    //Узнать трек номер заказа
    public int getOrderTrackNumber(OrderData orderData) {
        return orderCreate(orderData).then().extract().path("track");
    }

    //Отменить заказ
    public Response orderCancel(OrderData orderData) {
        return given(requestSpecification)
                .header("Content-type", "application/json")
                .when()
                .put(String.format("/api/v1/orders/cancel?track=%s", getOrderTrackNumber(orderData)));
    }

    //Получить все заказы
    public Response getAllOrders() {
        return given(requestSpecification).get("/api/v1/orders");
    }

    //Получить заказы курьера
    public Response getCourierOrders(int courierId) {
        return given(requestSpecification).queryParam("courierId", courierId).get("/api/v1/orders");
    }

    //Получить заказ по его номеру
    public Response getOrderByTrackNumber(OrderData orderData){
        return given(requestSpecification)
                .header("Content-type", "application/json")
                .when()
                .get(String.format("/api/v1/orders/track?t=%s", getOrderTrackNumber(orderData)));
    }

    //Получить id заказа по его трек номеру
    public int getOrderIdWithTrackNumber(OrderData orderData) {
        return getOrderByTrackNumber(orderData).then().extract().path("order.id");
    }

    //Принять заказ
    public void orderAcceptByCourier(OrderData orderData, int courierId) {
        given(requestSpecification)
                .header("Content-type", "application/json")
                .when()
                .put(String.format("/api/v1/orders/accept/%s?courierId=%s", getOrderIdWithTrackNumber(orderData), courierId));
    }
}
