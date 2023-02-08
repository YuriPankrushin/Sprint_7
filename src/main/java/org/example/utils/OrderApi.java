package org.example.utils;

import io.restassured.response.Response;
import org.example.OrderData;

import static io.restassured.RestAssured.given;

public class OrderApi extends BaseApi {

    final static String CREATE = "/api/v1/orders";
    final static String CANCEL = "/api/v1/orders/cancel?track=";
    final static String ORDERS = "/api/v1/orders";
    final static String ORDER = "/api/v1/orders/track?t=";
    final static String ACCEPT = "/api/v1/orders/accept/";

    //Создать заказ
    public Response orderCreate(OrderData orderData) {
        return given(requestSpecification)
                .header("Content-type", "application/json")
                .body(orderData)
                .when()
                .post(CREATE);
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
                .put(String.format(CANCEL + "%s", getOrderTrackNumber(orderData)));
    }

    //Получить все заказы
    public Response getAllOrders() {
        return given(requestSpecification).get(ORDERS);
    }

    //Получить заказы курьера
    public Response getCourierOrders(int courierId) {
        return given(requestSpecification).queryParam("courierId", courierId).get(ORDERS);
    }

    //Получить заказ по его номеру
    public Response getOrderByTrackNumber(OrderData orderData){
        return given(requestSpecification)
                .header("Content-type", "application/json")
                .when()
                .get(String.format(ORDER + "%s", getOrderTrackNumber(orderData)));
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
                .put(String.format(ACCEPT + "%s?courierId=%s", getOrderIdWithTrackNumber(orderData), courierId));
    }
}
