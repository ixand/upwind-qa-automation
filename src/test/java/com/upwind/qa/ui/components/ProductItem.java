package com.upwind.qa.ui.components;

import com.codeborne.selenide.SelenideElement;
import com.upwind.qa.ui.model.Product;

import static com.codeborne.selenide.Selectors.byTestId;

/**
 * One product row/card. SauceDemo renders products with the same markup
 * on the Products page and in the Cart, so both pages reuse it (via {@link ProductList}).
 */
public class ProductItem {

    private final SelenideElement root;

    public ProductItem(SelenideElement root) {
        this.root = root;
    }

    public SelenideElement name() {
        return root.$(byTestId("inventory-item-name"));
    }

    public SelenideElement price() {
        return root.$(byTestId("inventory-item-price"));
    }

    /** "Add to cart" button on the Products page, "Remove" button once added or in the Cart. */
    public SelenideElement actionButton() {
        return root.$("button");
    }

    /** Snapshot of the data currently displayed in this item. */
    public Product toProduct() {
        return Product.fromDisplayedText(name().getText(), price().getText());
    }
}
