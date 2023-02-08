package org.example.utils;

import io.restassured.response.Response;
import org.example.CourierData;

import static io.restassured.RestAssured.given;

public class CourierApi extends BaseApi {

    final static String CREATE = "/api/v1/courier";
    final static String LOGIN = "/api/v1/courier/login";
    final static String DELETE = "/api/v1/courier/{:id}";

    //Создать курьера
    public Response courierCreate(CourierData courierData) {
        return given(requestSpecification)
                        .header("Content-type", "application/json")
                        .body(courierData)
                        .when()
                        .post(CREATE);
    }

    //Авторизоваться курьером
    public Response courierLogin(CourierData courierLoginData) {
        return given(requestSpecification)
                .header("Content-type", "application/json")
                .body(courierLoginData)
                .when()
                .post(LOGIN);
    }

    //Узнать id курьера
    public int getCourierId(CourierData courierLoginData) {
        return courierLogin(courierLoginData).then().extract().path("id");
    }

    //Удаление курьера
    public void courierDelete(CourierData courierLoginData){
        given(requestSpecification).header("Content-type", "application/json").
                pathParam(":id", String.valueOf(getCourierId(courierLoginData))).when().delete(DELETE);
    }
}
