package com.upwind.qa.ui.model;

import java.util.Comparator;

/**
 * Sort options of the Products page.
 * Each option knows the value of its {@code <option>} element, the label shown to the user
 * and the order it must produce, so a test can check any option the same way.
 */
public enum SortOption {

    NAME_A_TO_Z("az", "Name (A to Z)", Comparator.comparing(Product::name)),
    NAME_Z_TO_A("za", "Name (Z to A)", Comparator.comparing(Product::name).reversed()),
    PRICE_LOW_TO_HIGH("lohi", "Price (low to high)", Comparator.comparing(Product::price)),
    PRICE_HIGH_TO_LOW("hilo", "Price (high to low)", Comparator.comparing(Product::price).reversed());

    private final String value;
    private final String label;
    private final Comparator<Product> expectedOrder;

    SortOption(String value, String label, Comparator<Product> expectedOrder) {
        this.value = value;
        this.label = label;
        this.expectedOrder = expectedOrder;
    }

    public String value() {
        return value;
    }

    public String label() {
        return label;
    }

    public Comparator<Product> expectedOrder() {
        return expectedOrder;
    }

    @Override
    public String toString() {
        return label;
    }
}
