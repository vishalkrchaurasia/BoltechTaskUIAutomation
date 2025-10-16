package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.Duration;

public class CTPLPlanPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public CTPLPlanPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    // On the plan selection confirmation page there might be a CTA to go to checkout
    public CTPLCheckoutPage proceedToCheckout() {
        // Attempt multiple strategies to find proceed button
        try {
            // try normal button
            By[] tries = new By[] {
                    By.cssSelector("button.proceed"),
                    By.cssSelector("button.btn-checkout"),
                    By.cssSelector("button.checkout"),
                    By.cssSelector("a[href*='/checkout']"),
                    By.cssSelector("button")
            };
            for (By b : tries) {
                try {
                    WebElement el = driver.findElement(b);
                    if (el.isDisplayed()) {
                        el.click();
                        waitForCheckoutUrl();
                        return new CTPLCheckoutPage(driver);
                    }
                } catch (Exception ignored) {}
            }
        } catch (Exception ignored) {}

        // fallback: try clicking first link with /checkout
        try {
            WebElement link = driver.findElement(By.cssSelector("a[href*='/checkout']"));
            link.click();
            waitForCheckoutUrl();
            return new CTPLCheckoutPage(driver);
        } catch (Exception e) {
            throw new RuntimeException("Could not proceed to checkout: " + e.getMessage());
        }
    }

    private void waitForCheckoutUrl() {
        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.urlContains("/checkout"));
    }
}
