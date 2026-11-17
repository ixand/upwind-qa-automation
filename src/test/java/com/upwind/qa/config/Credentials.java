package com.upwind.qa.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Username/password pair. Serialised as {@code {"username": ..., "password": ...}}
 * so it can also be sent as the Restful Booker auth request body.
 */
public record Credentials(
        @JsonProperty("username") String username,
        @JsonProperty("password") String password) {

    /** Keeps the password out of logs and reports. */
    @Override
    public String toString() {
        return "Credentials[username=" + username + "]";
    }
}
