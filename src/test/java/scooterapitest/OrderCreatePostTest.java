package scooterapitest;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.OrderData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.hasKey;

@RunWith(Parameterized.class)
public class OrderCreatePostTest {

    private final OrderData orderData;

    public OrderCreatePostTest(OrderData orderData) {
        this.orderData = orderData;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][] {
                {new OrderData("Юрий", "Панкрушин", "Москва, Красная площадь, 1", "32", "79267777777", 7, "2023-02-15", "Сдачу оставь себе", List.of("GREY", "BLACK"))},
                {new OrderData("Юрий", "Панкрушин", "Москва, Красная площадь, 1", "32", "79267777777", 5, "2023-02-15", "Сдачу оставь себе", List.of("BLACK"))},
                {new OrderData("Юрий", "Панкрушин", "Москва, Красная площадь, 1", "32", "79267777777", 3, "2023-02-15", "Сдачу оставь себе", List.of("GREY"))},
                {new OrderData("Юрий", "Панкрушин", "Москва, Красная площадь, 1", "32", "79267777777", 1, "2023-02-15", "Сдачу оставь себе", null)},
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Создать заказ")
    @Description("Проверить, что заказ успешно создан")
    public void checkThatOrderWasCreatedWithTrackValue() {
        //Send create order request
        Response newOrder =
                given()
                        .header("Content-type", "application/json")
                        .body(orderData)
                        .when()
                        .post("/api/v1/orders");

        //Check successful and correct response
        newOrder.then().assertThat().body("$", hasKey("track"))
                .and()
                .statusCode(SC_CREATED);

        /** Clear test data */
        //Cancel order
        int trackNumber = newOrder.then().extract().path("track");
        given().header("Content-type", "application/json").when().put(String.format("/api/v1/orders/cancel?track=%s", trackNumber));
    }
}