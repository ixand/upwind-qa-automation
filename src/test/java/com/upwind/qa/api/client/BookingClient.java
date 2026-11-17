package com.upwind.qa.api.client;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Thin wrapper over the /booking endpoints.
 * It only sends requests and returns the raw {@link Response}: the test decides what is expected,
 * so the same client serves positive and negative scenarios.
 */
public class BookingClient {

    private static final String BOOKINGS = "/booking";
    private static final String BOOKING_BY_ID = "/booking/{id}";

    /**
     * @param body a {@link com.upwind.qa.api.model.Booking}, or any other object/raw JSON string
     *             for invalid-payload scenarios
     */
    @Step("POST /booking")
    public Response create(Object body) {
        return given(ApiSpecs.jsonRequest())
                .body(body)
                .when()
                .post(BOOKINGS);
    }

    @Step("GET /booking/{id}")
    public Response get(int id) {
        return given(ApiSpecs.jsonRequest())
                .pathParam("id", id)
                .when()
                .get(BOOKING_BY_ID);
    }

    @Step("DELETE /booking/{id}")
    public Response delete(int id, String token) {
        return given(ApiSpecs.jsonRequest())
                .pathParam("id", id)
                .cookie("token", token)
                .when()
                .delete(BOOKING_BY_ID);
    }
}
