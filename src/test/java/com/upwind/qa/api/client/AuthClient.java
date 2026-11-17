package com.upwind.qa.api.client;

import com.upwind.qa.api.model.AuthResponse;
import com.upwind.qa.config.Credentials;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class AuthClient {

    private static final String AUTH = "/auth";

    @Step("POST /auth as {credentials.username}")
    public Response createToken(Credentials credentials) {
        return given(ApiSpecs.jsonRequest())
                .body(credentials)
                .when()
                .post(AUTH);
    }

    /** Convenience for tests that need a token as a precondition, not as the subject under test. */
    public String obtainToken(Credentials credentials) {
        AuthResponse response = createToken(credentials)
                .then()
                .statusCode(200)
                .extract()
                .as(AuthResponse.class);
        if (response.token() == null) {
            throw new IllegalStateException("No auth token returned, reason: " + response.reason());
        }
        return response.token();
    }
}
