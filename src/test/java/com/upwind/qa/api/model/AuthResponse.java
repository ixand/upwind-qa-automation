package com.upwind.qa.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response of {@code POST /auth}. On success only {@code token} is set,
 * on failure only {@code reason} (the API still answers 200 OK).
 */
public record AuthResponse(
        @JsonProperty("token") String token,
        @JsonProperty("reason") String reason) {
}
