package com.upwind.qa.ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import com.upwind.qa.ui.components.ProductList;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selectors.byTestId;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

public class CartPage {

    private final SelenideElement title = $(byTestId("title"));
    private final ElementsCollection quantities = $$(byTestId("item-quantity"));

    private final ProductList products = new ProductList();

    public CartPage shouldBeOpened() {
        webdriver().shouldHave(urlContaining("/cart.html"));
        title.shouldHave(exactText("Your Cart"));
        return this;
    }

    public ProductList products() {
        return products;
    }

    /** Quantity of every cart line, in display order. */
    public ElementsCollection quantities() {
        return quantities;
    }
}
