package com.upwind.qa.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/** Serialised as {@code {"checkin": "2026-12-01", "checkout": "2026-12-05"}}. */
public record BookingDates(
        @JsonProperty("checkin") LocalDate checkIn,
        @JsonProperty("checkout") LocalDate checkOut) {
}
