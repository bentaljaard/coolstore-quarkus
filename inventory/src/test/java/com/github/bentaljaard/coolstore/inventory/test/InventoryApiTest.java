package com.github.bentaljaard.coolstore.inventory.test;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class InventoryApiTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/availability")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }

}