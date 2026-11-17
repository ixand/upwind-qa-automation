package com.upwind.qa.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/** Response of {@code POST /booking}: the generated id plus the stored booking. */
public record CreatedBooking(
        @JsonProperty("bookingid") Integer bookingId,
        @JsonProperty("booking") Booking booking) {
}
