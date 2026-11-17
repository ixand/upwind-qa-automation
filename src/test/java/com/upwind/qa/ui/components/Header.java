package com.upwind.qa.ui.components;

import com.codeborne.selenide.SelenideElement;
import com.upwind.qa.ui.pages.CartPage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selectors.byTestId;

/** Top bar shown on every page after login. */
public class Header {

    private final SelenideElement cartLink = $(byTestId("shopping-cart-link"));
    private final SelenideElement cartBadge = $(byTestId("shopping-cart-badge"));

    /** Number of items in the cart. The badge is not rendered at all when the cart is empty. */
    public SelenideElement cartBadge() {
        return cartBadge;
    }

    @Step("Open the cart")
    public CartPage openCart() {
        cartLink.click();
        return new CartPage().shouldBeOpened();
    }
}
