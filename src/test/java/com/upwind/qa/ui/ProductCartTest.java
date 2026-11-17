package com.upwind.qa.ui;

import com.upwind.qa.config.TestConfig;
import com.upwind.qa.ui.model.Product;
import com.upwind.qa.ui.model.SortOption;
import com.upwind.qa.ui.pages.CartPage;
import com.upwind.qa.ui.pages.LoginPage;
import com.upwind.qa.ui.pages.ProductsPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.exactText;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Products and cart")
class ProductCartTest extends BaseUiTest {

    /**
     * Sorts the list, checks the order, then adds the product that the sort put first
     * (a different product for every option) and checks it in the cart.
     */
    @ParameterizedTest(name = "sorted by \"{0}\"")
    @EnumSource(SortOption.class)
    @DisplayName("Top product after sorting can be added to the cart")
    void topProductAfterSortingCanBeAddedToCart(SortOption sortOption) {
        ProductsPage productsPage = LoginPage.open().loginAs(TestConfig.uiUser());
        List<Product> productsBeforeSorting = productsPage.products().snapshot();

        productsPage.sortBy(sortOption);

        List<Product> sortedProducts = productsPage.products().snapshot();
        assertThat(sortedProducts)
                .as("products sorted by '%s'", sortOption)
                .isSortedAccordingTo(sortOption.expectedOrder())
                .containsExactlyInAnyOrderElementsOf(productsBeforeSorting);

        Product selectedProduct = sortedProducts.get(0);
        productsPage.addToCart(selectedProduct.name());
        productsPage.header().cartBadge().shouldHave(exactText("1"));

        CartPage cartPage = productsPage.header().openCart();
        cartPage.products().names().shouldHave(exactTexts(selectedProduct.name()));
        cartPage.products().prices().shouldHave(exactTexts(selectedProduct.displayedPrice()));
        cartPage.quantities().shouldHave(exactTexts("1"));
    }
}
