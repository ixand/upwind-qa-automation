package com.upwind.qa.ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.upwind.qa.config.Credentials;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byTestId;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

    private final SelenideElement usernameInput = $(byTestId("username"));
    private final SelenideElement passwordInput = $(byTestId("password"));
    private final SelenideElement loginButton = $(byTestId("login-button"));
    private final SelenideElement errorMessage = $(byTestId("error"));

    @Step("Open the login page")
    public static LoginPage open() {
        // Relative to selenide.baseUrl
        Selenide.open("/");
        return new LoginPage().shouldBeOpened();
    }

    public LoginPage shouldBeOpened() {
        loginButton.shouldBe(visible);
        return this;
    }

    @Step("Log in as {credentials.username}")
    public ProductsPage loginAs(Credentials credentials) {
        submit(credentials);
        return new ProductsPage().shouldBeOpened();
    }

    /** Submits the form without expecting success, for negative scenarios. */
    @Step("Submit login form as '{credentials.username}'")
    public LoginPage submit(Credentials credentials) {
        usernameInput.setValue(credentials.username());
        passwordInput.setValue(credentials.password());
        loginButton.click();
        return this;
    }

    public SelenideElement errorMessage() {
        return errorMessage;
    }
}
