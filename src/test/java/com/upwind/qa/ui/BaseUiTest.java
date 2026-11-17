package com.upwind.qa.ui;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

/**
 * Common setup for UI tests.
 * <ul>
 *     <li>{@code @Tag("ui")} is inherited, so {@code -Dgroups=ui} selects every subclass.</li>
 *     <li>Browser settings come from {@code selenide.properties}.</li>
 *     <li>Every test gets a fresh browser: no cookies or cart state leak between tests
 *     (SauceDemo keeps the cart in localStorage).</li>
 * </ul>
 */
@Tag("ui")
public abstract class BaseUiTest {

    private static final String ALLURE_LISTENER = "allure";

    @BeforeEach
    void attachAllureListener() {
        // Selenide listeners are per-thread, so register in @BeforeEach (works with parallel execution).
        SelenideLogger.addListener(ALLURE_LISTENER, new AllureSelenide()
                .screenshots(true)
                .savePageSource(false));
    }

    @AfterEach
    void closeBrowser() {
        Selenide.closeWebDriver();
        SelenideLogger.removeListener(ALLURE_LISTENER);
    }
}
