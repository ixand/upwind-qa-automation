package com.upwind.qa.api.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.upwind.qa.api.client.ApiSpecs;
import com.upwind.qa.api.model.Booking;
import com.upwind.qa.api.model.BookingDates;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Test data for bookings. Every call returns a new booking with a unique last name,
 * so tests running in parallel (or other users of the public API) never share data.
 */
public final class BookingTestData {

    private BookingTestData() {
    }

    public static Booking validBooking() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        LocalDate checkIn = LocalDate.now().plusDays(random.nextInt(1, 90));
        LocalDate checkOut = checkIn.plusDays(random.nextInt(1, 15));
        return new Booking(
                "Alice",
                "Tester-" + UUID.randomUUID().toString().substring(0, 8),
                random.nextInt(50, 1000),
                random.nextBoolean(),
                new BookingDates(checkIn, checkOut),
                "Breakfast");
    }

    /**
     * A valid booking as raw JSON fields with one field removed.
     *
     * @param jsonField API field name, e.g. {@code "firstname"}
     */
    public static Map<String, Object> validBookingWithout(String jsonField) {
        Map<String, Object> fields = ApiSpecs.objectMapper()
                .convertValue(validBooking(), new TypeReference<Map<String, Object>>() {
                });
        if (fields.remove(jsonField) == null) {
            throw new IllegalArgumentException("Booking has no field '" + jsonField + "'");
        }
        return fields;
    }
}
