package com.upwind.qa.api;

import com.upwind.qa.api.client.AuthClient;
import com.upwind.qa.api.client.BookingClient;
import com.upwind.qa.config.TestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Common setup for API tests.
 * Bookings registered with {@link #deleteAfterTest(int)} are removed after the test,
 * so the tests leave no data behind in the shared public API.
 */
@Tag("api")
public abstract class BaseApiTest {

    protected final BookingClient bookingClient = new BookingClient();
    protected final AuthClient authClient = new AuthClient();

    // JUnit creates a new test class instance per test method, so this list is per test.
    private final List<Integer> bookingsToDelete = new ArrayList<>();

    protected void deleteAfterTest(int bookingId) {
        bookingsToDelete.add(bookingId);
    }

    @AfterEach
    void deleteCreatedBookings() {
        if (bookingsToDelete.isEmpty()) {
            return;
        }
        String token = authClient.obtainToken(TestConfig.apiAdmin());
        // Best-effort cleanup: the result is not asserted, a failed cleanup must not fail the test.
        bookingsToDelete.forEach(id -> bookingClient.delete(id, token));
    }
}
