package org.example.utils;

import io.restassured.response.Response;
import org.example.CourierData;

import static io.restassured.RestAssured.given;

public class CourierApi extends BaseApi {

    //Создать курьера
    public Response courierCreate(CourierData courierData) {
        return given(requestSpecification)
                        .header("Content-type", "application/json")
                        .body(courierData)
                        .when()
                        .post("/api/v1/courier");
    }

    //Авторизоваться курьером
    public Response courierLogin(CourierData courierLoginData) {
        return given(requestSpecification)
                .header("Content-type", "application/json")
                .body(courierLoginData)
                .when()
                .post("/api/v1/courier/login");
    }

    //Узнать id курьера
    public int getCourierId(CourierData courierLoginData) {
        return courierLogin(courierLoginData).then().extract().path("id");
    }

    //Удаление курьера
    public void courierDelete(CourierData courierLoginData){
        given(requestSpecification).header("Content-type", "application/json").
                pathParam(":id", String.valueOf(getCourierId(courierLoginData))).when().delete("/api/v1/courier/{:id}");
    }
}
