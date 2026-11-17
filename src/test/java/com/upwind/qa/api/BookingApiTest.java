package com.upwind.qa.api;

import com.upwind.qa.api.data.BookingTestData;
import com.upwind.qa.api.model.Booking;
import com.upwind.qa.api.model.CreatedBooking;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayName("Booking API")
class BookingApiTest extends BaseApiTest {

    @Test
    @DisplayName("Created booking is returned by GET /booking/{id} with the same data")
    void createdBookingCanBeRetrievedById() {
        Booking booking = BookingTestData.validBooking();

        CreatedBooking created = bookingClient.create(booking)
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .as(CreatedBooking.class);
        deleteAfterTest(created.bookingId());

        assertThat(created.bookingId()).as("generated booking id").isPositive();
        assertThat(created.booking()).as("booking returned by POST").isEqualTo(booking);

        Booking retrieved = bookingClient.get(created.bookingId())
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body(matchesJsonSchemaInClasspath("schemas/booking.schema.json"))
                .extract()
                .as(Booking.class);

        // Soft assertions: one run reports every mismatching field, not only the first one.
        assertSoftly(softly -> {
            softly.assertThat(retrieved.firstName()).as("firstname").isEqualTo(booking.firstName());
            softly.assertThat(retrieved.lastName()).as("lastname").isEqualTo(booking.lastName());
            softly.assertThat(retrieved.totalPrice()).as("totalprice").isEqualTo(booking.totalPrice());
            softly.assertThat(retrieved.depositPaid()).as("depositpaid").isEqualTo(booking.depositPaid());
            softly.assertThat(retrieved.bookingDates()).as("bookingdates").isEqualTo(booking.bookingDates());
            softly.assertThat(retrieved.additionalNeeds()).as("additionalneeds").isEqualTo(booking.additionalNeeds());
        });
    }
}
