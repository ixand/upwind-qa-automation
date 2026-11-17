package com.upwind.qa.api;

import com.upwind.qa.api.data.BookingTestData;
import com.upwind.qa.api.model.AuthResponse;
import com.upwind.qa.api.model.Booking;
import com.upwind.qa.api.model.CreatedBooking;
import com.upwind.qa.config.Credentials;
import com.upwind.qa.config.TestConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Negative scenarios. Expected status codes are the ones Restful Booker actually returns
 * (checked against the live API); where they differ from common REST practice it is noted
 * in a comment next to the check.
 */
@DisplayName("Booking API: negative scenarios")
class BookingNegativeApiTest extends BaseApiTest {

    @ParameterizedTest(name = "without \"{0}\"")
    @ValueSource(strings = {"firstname", "lastname", "totalprice", "depositpaid", "bookingdates"})
    @DisplayName("Booking without a required field is rejected")
    void bookingWithoutRequiredFieldIsRejected(String missingField) {
        bookingClient.create(BookingTestData.validBookingWithout(missingField))
                .then()
                // A 400 would be expected; Restful Booker answers 500. The test pins the current
                // behaviour so that accepting an incomplete booking (2xx) is detected.
                .statusCode(500);
    }

    @Test
    @DisplayName("Malformed JSON is rejected with 400 Bad Request")
    void malformedJsonIsRejected() {
        bookingClient.create("{\"firstname\": \"Alice\", ")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("Deleted booking can no longer be retrieved")
    void deletedBookingIsNotFound() {
        int bookingId = createBooking(BookingTestData.validBooking()).bookingId();
        String token = authClient.obtainToken(TestConfig.apiAdmin());

        bookingClient.delete(bookingId, token)
                .then()
                // Restful Booker answers 201 Created to a successful DELETE.
                .statusCode(201);

        bookingClient.get(bookingId)
                .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("Booking cannot be deleted with an invalid token")
    void bookingCannotBeDeletedWithInvalidToken() {
        Booking booking = BookingTestData.validBooking();
        int bookingId = createBooking(booking).bookingId();
        deleteAfterTest(bookingId);

        bookingClient.delete(bookingId, "invalid-token")
                .then()
                .statusCode(403);

        Booking stillStored = bookingClient.get(bookingId)
                .then()
                .statusCode(200)
                .extract()
                .as(Booking.class);
        assertThat(stillStored).as("booking after rejected delete").isEqualTo(booking);
    }

    @Test
    @DisplayName("Auth with wrong password returns no token")
    void authWithWrongPasswordReturnsNoToken() {
        Credentials wrongPassword = new Credentials(TestConfig.apiAdmin().username(), "wrong-password");

        AuthResponse response = authClient.createToken(wrongPassword)
                .then()
                // Restful Booker reports failed auth in the body, not with 401.
                .statusCode(200)
                .extract()
                .as(AuthResponse.class);

        assertThat(response.token()).as("token").isNull();
        assertThat(response.reason()).as("reason").isEqualTo("Bad credentials");
    }

    private CreatedBooking createBooking(Booking booking) {
        return bookingClient.create(booking)
                .then()
                .statusCode(200)
                .extract()
                .as(CreatedBooking.class);
    }
}
