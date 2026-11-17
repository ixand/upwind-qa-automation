package com.upwind.qa.ui;

import com.codeborne.selenide.Selenide;
import com.upwind.qa.config.Credentials;
import com.upwind.qa.config.TestConfig;
import com.upwind.qa.ui.pages.LoginPage;
import com.upwind.qa.ui.pages.ProductsPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.codeborne.selenide.CollectionCondition.allMatch;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

@DisplayName("Login")
class LoginTest extends BaseUiTest {

    @Test
    @DisplayName("Standard user logs in and sees the Products page with products")
    void standardUserLogsInAndSeesProducts() {
        ProductsPage productsPage = LoginPage.open()
                .loginAs(TestConfig.uiUser()); // waits until the Products page is opened

        productsPage.products().items().shouldHave(sizeGreaterThan(0));
        productsPage.products().names().shouldHave(allMatch("every product has a name",
                name -> !name.getText().isBlank()));
        productsPage.products().prices().shouldHave(allMatch("every product has a price",
                price -> price.getText().matches("\\$\\d+\\.\\d{2}")));
    }

    @ParameterizedTest(name = "{0}")
    @CsvSource(delimiter = '|', textBlock = """
            locked out user      | locked_out_user | secret_sauce | Epic sadface: Sorry, this user has been locked out.
            wrong password       | standard_user   | wrong_secret | Epic sadface: Username and password do not match any user in this service
            empty username       | ''              | secret_sauce | Epic sadface: Username is required
            empty password       | standard_user   | ''           | Epic sadface: Password is required
            """)
    @DisplayName("Login is rejected with a clear error:")
    void loginIsRejected(String scenario, String username, String password, String expectedError) {
        LoginPage loginPage = LoginPage.open()
                .submit(new Credentials(username, password));

        loginPage.errorMessage().shouldHave(exactText(expectedError));
        loginPage.shouldBeOpened();
    }

    @Test
    @DisplayName("Products page is not reachable without logging in")
    void productsPageRequiresLogin() {
        Selenide.open("/inventory.html");

        LoginPage loginPage = new LoginPage().shouldBeOpened();
        loginPage.errorMessage()
                .shouldHave(exactText("Epic sadface: You can only access '/inventory.html' when you are logged in."));
        webdriver().shouldNotHave(urlContaining("/inventory.html"));
    }
}
