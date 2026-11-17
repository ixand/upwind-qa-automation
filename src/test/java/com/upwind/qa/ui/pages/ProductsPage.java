package com.upwind.qa.ui.pages;

import com.codeborne.selenide.SelenideElement;
import com.upwind.qa.ui.components.Header;
import com.upwind.qa.ui.components.ProductList;
import com.upwind.qa.ui.model.SortOption;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selectors.byTestId;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

/** The inventory page (title "Products") shown after a successful login. */
public class ProductsPage {

    private final SelenideElement title = $(byTestId("title"));
    private final SelenideElement sortDropdown = $(byTestId("product-sort-container"));
    private final SelenideElement activeSortOption = $(byTestId("active-option"));

    private final Header header = new Header();
    private final ProductList products = new ProductList();

    public ProductsPage shouldBeOpened() {
        webdriver().shouldHave(urlContaining("/inventory.html"));
        title.shouldHave(exactText("Products"));
        products.items().shouldHave(sizeGreaterThan(0));
        return this;
    }

    @Step("Sort products by \"{option}\"")
    public ProductsPage sortBy(SortOption option) {
        sortDropdown.selectOptionByValue(option.value());
        // The label next to the dropdown is re-rendered together with the list,
        // so once it shows the new option the list is in its new order.
        activeSortOption.shouldHave(exactText(option.label()));
        return this;
    }

    @Step("Add \"{productName}\" to the cart")
    public ProductsPage addToCart(String productName) {
        products.item(productName).actionButton()
                .shouldHave(exactText("Add to cart"))
                .click();
        return this;
    }

    public ProductList products() {
        return products;
    }

    public Header header() {
        return header;
    }
}
