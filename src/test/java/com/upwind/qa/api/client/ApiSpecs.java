package com.upwind.qa.api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.upwind.qa.config.TestConfig;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.LogConfig;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/** Shared REST Assured configuration for every API client. */
public final class ApiSpecs {

    /** Writes/reads {@link java.time.LocalDate} as "yyyy-MM-dd", the format used by Restful Booker. */
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    private ApiSpecs() {
    }

    /** JSON in, JSON out; request and response are logged to the console only when a check fails. */
    public static RequestSpecification jsonRequest() {
        return new RequestSpecBuilder()
                .setBaseUri(TestConfig.apiBaseUrl())
                .setContentType(ContentType.JSON)
                // Exactly "application/json": ContentType.JSON would also list text/json etc.,
                // and Restful Booker answers such an Accept header with 418 I'm a Teapot.
                .setAccept("application/json")
                .setConfig(RestAssuredConfig.config()
                        .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                                .jackson2ObjectMapperFactory((type, charset) -> OBJECT_MAPPER))
                        .logConfig(LogConfig.logConfig()
                                .enableLoggingOfRequestAndResponseIfValidationFails()
                                .blacklistHeader("Cookie")))
                .addFilter(new AllureRestAssured())
                .build();
    }

    public static ObjectMapper objectMapper() {
        return OBJECT_MAPPER;
    }
}
