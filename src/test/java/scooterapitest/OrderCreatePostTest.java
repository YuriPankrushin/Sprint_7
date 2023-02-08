package scooterapitest;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.example.OrderData;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;

import static org.apache.http.HttpStatus.SC_CREATED;
import static org.hamcrest.Matchers.hasKey;

@RunWith(Parameterized.class)
public class OrderCreatePostTest extends BaseTest {

    private final OrderData orderData;

    public OrderCreatePostTest(OrderData orderData) {
        this.orderData = orderData;
    }

    /** Тестовые данные */
    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][] {
                {new OrderData("Юрий", "Панкрушин", "Москва, Красная площадь, 1", "32", "79267777777", 7, "2023-02-15", "Сдачу оставь себе", List.of("GREY", "BLACK"))},
                {new OrderData("Юрий", "Панкрушин", "Москва, Красная площадь, 1", "32", "79267777777", 5, "2023-02-15", "Сдачу оставь себе", List.of("BLACK"))},
                {new OrderData("Юрий", "Панкрушин", "Москва, Красная площадь, 1", "32", "79267777777", 3, "2023-02-15", "Сдачу оставь себе", List.of("GREY"))},
                {new OrderData("Юрий", "Панкрушин", "Москва, Красная площадь, 1", "32", "79267777777", 1, "2023-02-15", "Сдачу оставь себе", null)},
        };
    }

    @After
    public void testDataClear(){
        /** Удаление тестовых данных */
        //Удаление заказов
        orderApi.orderCancel(orderData);
    }

    @Test
    @DisplayName("Создать заказ")
    @Description("Проверить, что заказ успешно создан")
    public void checkThatOrderWasCreatedWithTrackValue() {
        //Создать заказ
        Response newOrder = orderApi.orderCreate(orderData);
        //Проверить, что ответ содержит трек номер и правильный статус код
        newOrder.then().assertThat().body("$", hasKey("track"))
                .and()
                .statusCode(SC_CREATED);
    }
}