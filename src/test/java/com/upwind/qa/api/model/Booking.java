package com.upwind.qa.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Booking resource of Restful Booker: the body of {@code POST /booking}
 * and of the {@code GET /booking/{id}} response.
 * Java names follow Java conventions, {@link JsonProperty} maps them to the API names.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record Booking(
        @JsonProperty("firstname") String firstName,
        @JsonProperty("lastname") String lastName,
        @JsonProperty("totalprice") Integer totalPrice,
        @JsonProperty("depositpaid") Boolean depositPaid,
        @JsonProperty("bookingdates") BookingDates bookingDates,
        @JsonProperty("additionalneeds") String additionalNeeds) {
}
