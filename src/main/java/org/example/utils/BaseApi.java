package org.example.utils;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

public class BaseApi {
    public RequestSpecification requestSpecification =
            RestAssured.given()
                    .baseUri("http://qa-scooter.praktikum-services.ru/");
}
