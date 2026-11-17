package com.upwind.qa.ui.components;

import com.codeborne.selenide.ElementsCollection;
import com.upwind.qa.ui.model.Product;

import java.util.List;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Selectors.byTestId;
import static com.codeborne.selenide.Selenide.$$;

/** List of products on the Products page or in the Cart. */
public class ProductList {

    private static final String ITEM_SELECTOR = "[data-test=inventory-item]";

    private final ElementsCollection items = $$(ITEM_SELECTOR);
    private final ElementsCollection names = $$(byTestId("inventory-item-name"));
    private final ElementsCollection prices = $$(byTestId("inventory-item-price"));

    public ElementsCollection items() {
        return items;
    }

    public ElementsCollection names() {
        return names;
    }

    public ElementsCollection prices() {
        return prices;
    }

    /** Finds a product by its exact visible name (more stable than an index). */
    public ProductItem item(String productName) {
        return new ProductItem(names.findBy(exactText(productName)).ancestor(ITEM_SELECTOR));
    }

    /**
     * Snapshot of all displayed products, in display order.
     * Plain values do not auto-wait, so call it only after a condition has confirmed the list is in the
     * expected state.
     */
    public List<Product> snapshot() {
        return items.asFixedIterable().stream()
                .map(ProductItem::new)
                .map(ProductItem::toProduct)
                .toList();
    }
}
