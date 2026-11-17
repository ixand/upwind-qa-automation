package com.upwind.qa.ui.model;

import java.math.BigDecimal;

/**
 * A product as the user sees it: name and price.
 * The price is a {@link BigDecimal} so that sorting by price compares numbers, not strings.
 */
public record Product(String name, BigDecimal price) {

    private static final String CURRENCY = "$";

    /** Creates a product from texts shown on the page, e.g. {@code ("Sauce Labs Onesie", "$7.99")}. */
    public static Product fromDisplayedText(String name, String displayedPrice) {
        String amount = displayedPrice.trim();
        if (!amount.startsWith(CURRENCY)) {
            throw new IllegalArgumentException("Unexpected price format: '" + displayedPrice + "'");
        }
        return new Product(name.trim(), new BigDecimal(amount.substring(CURRENCY.length())));
    }

    /** The price exactly as SauceDemo renders it, e.g. {@code $7.99}. */
    public String displayedPrice() {
        return CURRENCY + price.toPlainString();
    }
}
