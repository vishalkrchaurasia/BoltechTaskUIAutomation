package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CTPLCheckoutPage {
    private WebDriver driver;
    private WebDriverWait wait;

    public CTPLCheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
        
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/checkout/payment"),
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("fwd-checkout-summary"))
        ));
    }


    private SearchContext getShadowRoot(WebElement host) {
        try {
            return host.getShadowRoot();
        } catch (Throwable t) {
            return (SearchContext) ((JavascriptExecutor) driver)
                    .executeScript("return arguments[0].shadowRoot", host);
        }
    }

    public boolean verifySummaryDetails() {

        try {
            WebElement summaryHost = driver.findElement(By.cssSelector("fwd-checkout-summary"));
            SearchContext shadow = getShadowRoot(summaryHost);

            String price = getTextFromShadow(shadow, Arrays.asList(
                    By.cssSelector(".current-price"),
                    By.cssSelector(".price"),
                    By.cssSelector(".summary-price"),
                    By.cssSelector(".total-amount")
            ));

            String productName = getTextFromShadow(shadow, Arrays.asList(
                    By.cssSelector(".product-name"),
                    By.cssSelector(".title")
            ));

            
            String provider = getTextFromShadow(shadow, Arrays.asList(
                    By.cssSelector(".provider"),
                    By.cssSelector(".provider-name")
            ));

            
            String coverage = getTextFromShadow(shadow, Arrays.asList(
                    By.cssSelector(".coverage-period"),
                    By.cssSelector(".period"),
                    By.cssSelector(".coverage")
            ));

            // Policy Start Date visible in summary
            String dateInSummary = getTextFromShadow(shadow, Arrays.asList(
                    By.cssSelector(".policy-start-date"),
                    By.cssSelector(".start-date"),
                    By.cssSelector(".policy-date")
            ));

            boolean covOk = coverage != null && coverage.toLowerCase().contains("12");

            boolean providerOk = provider != null && (provider.toLowerCase().contains("ergo"));

            ZoneId thailand = ZoneId.of("Asia/Bangkok");
            LocalDate todayTH = LocalDate.now(thailand);
            DateTimeFormatter f1 = DateTimeFormatter.ofPattern("d/M/yyyy");
            DateTimeFormatter f2 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String d1 = todayTH.format(f1);
            String d2 = todayTH.format(f2);

            boolean dateOk = dateInSummary != null && (dateInSummary.contains(d1) || dateInSummary.contains(d2));

            boolean priceOk = price != null && price.trim().length() > 0;
            boolean productOk = productName != null && productName.trim().length() > 0;

            System.out.println("Summary - price:" + price + ", product:" + productName + ", provider:" + provider + ", coverage:" + coverage + ", date:" + dateInSummary);

            return covOk && providerOk && dateOk && priceOk && productOk;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getTextFromShadow(SearchContext shadow, List<By> selectors) {
        for (By sel : selectors) {
            try {
                WebElement el = shadow.findElement(sel);
                String t = el.getText();
                if (t != null && !t.trim().isEmpty()) return t.trim();
            } catch (Exception ignored) {}
        }
        return null;
    }

    public void changePolicyStartDateToMax() {

        try {
            WebElement dateInput = driver.findElement(By.cssSelector("input[name='policyStartDate'], input[aria-label*='Policy start'], input[type='date']"));

            try {
                String max = dateInput.getAttribute("max");
                if (max != null && !max.isEmpty()) {
                    dateInput.clear();
                    dateInput.sendKeys(max);
                    dateInput.sendKeys(Keys.TAB);
                    waitABit();
                    return;
                }
            } catch (Exception ignored) {}

            LocalDate thailandNow = LocalDate.now(ZoneId.of("Asia/Bangkok"));
            LocalDate maxDate = thailandNow.plusMonths(12); 
            String val = maxDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            dateInput.clear();
            dateInput.sendKeys(val);
            dateInput.sendKeys(Keys.TAB);
            waitABit();
        } catch (Exception e) {
          
            try {
                WebElement summaryHost = driver.findElement(By.cssSelector("fwd-checkout-summary"));
                SearchContext shadow = (SearchContext) ((JavascriptExecutor) driver).executeScript("return arguments[0].shadowRoot", summaryHost);
                WebElement dateInput = shadow.findElement(By.cssSelector("input[type='date'], input[name='policyStartDate']"));
                String max = dateInput.getAttribute("max");
                if (max != null && !max.isEmpty()) {
                    dateInput.clear();
                    dateInput.sendKeys(max);
                    dateInput.sendKeys(Keys.TAB);
                    waitABit();
                    return;
                }
                LocalDate maxDate = LocalDate.now(ZoneId.of("Asia/Bangkok")).plusMonths(12);
                dateInput.clear();
                dateInput.sendKeys(maxDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                dateInput.sendKeys(Keys.TAB);
                waitABit();
            } catch (Exception ex) {
                throw new RuntimeException("Unable to change policy start date to max: " + ex.getMessage());
            }
        }
    }

    public void changePolicyStartDateToMin() {
        try {
            WebElement dateInput = driver.findElement(By.cssSelector("input[name='policyStartDate'], input[aria-label*='Policy start'], input[type='date']"));
            try {
                String min = dateInput.getAttribute("min");
                if (min != null && !min.isEmpty()) {
                    dateInput.clear();
                    dateInput.sendKeys(min);
                    dateInput.sendKeys(Keys.TAB);
                    waitABit();
                    return;
                }
            } catch (Exception ignored) {}

            LocalDate minDate = LocalDate.now(ZoneId.of("Asia/Bangkok"));
            dateInput.clear();
            dateInput.sendKeys(minDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            dateInput.sendKeys(Keys.TAB);
            waitABit();
        } catch (Exception e) {
            try {
                WebElement summaryHost = driver.findElement(By.cssSelector("fwd-checkout-summary"));
                SearchContext shadow = (SearchContext) ((JavascriptExecutor) driver).executeScript("return arguments[0].shadowRoot", summaryHost);
                WebElement dateInput = shadow.findElement(By.cssSelector("input[type='date'], input[name='policyStartDate']"));
                String min = dateInput.getAttribute("min");
                if (min != null && !min.isEmpty()) {
                    dateInput.clear();
                    dateInput.sendKeys(min);
                    dateInput.sendKeys(Keys.TAB);
                    waitABit();
                    return;
                }
                LocalDate minDate = LocalDate.now(ZoneId.of("Asia/Bangkok"));
                dateInput.clear();
                dateInput.sendKeys(minDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                dateInput.sendKeys(Keys.TAB);
                waitABit();
            } catch (Exception ex) {
                throw new RuntimeException("Unable to change policy start date to min: " + ex.getMessage());
            }
        }
    }

    private void waitABit() {
        try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
    }


    public boolean verifyPolicyStartDateChangedToMax() {
        try {
            WebElement summaryHost = driver.findElement(By.cssSelector("fwd-checkout-summary"));
            SearchContext shadow = getShadowRoot(summaryHost);
            String dateInSummary = getTextFromShadow(shadow, Arrays.asList(
                    By.cssSelector(".policy-start-date"),
                    By.cssSelector(".start-date"),
                    By.cssSelector(".policy-date")
            ));
            if (dateInSummary == null) return false;

            LocalDate expected = LocalDate.now(ZoneId.of("Asia/Bangkok")).plusMonths(12);
            String expectedStr = expected.format(DateTimeFormatter.ofPattern("d/M/yyyy"));
            String expectedStr2 = expected.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return dateInSummary.contains(expectedStr) || dateInSummary.contains(expectedStr2);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean verifyPolicyStartDateChangedToMin() {
        try {
            WebElement summaryHost = driver.findElement(By.cssSelector("fwd-checkout-summary"));
            SearchContext shadow = getShadowRoot(summaryHost);
            String dateInSummary = getTextFromShadow(shadow, Arrays.asList(
                    By.cssSelector(".policy-start-date"),
                    By.cssSelector(".start-date"),
                    By.cssSelector(".policy-date")
            ));
            if (dateInSummary == null) return false;
            LocalDate expected = LocalDate.now(ZoneId.of("Asia/Bangkok"));
            String expectedStr = expected.format(DateTimeFormatter.ofPattern("d/M/yyyy"));
            String expectedStr2 = expected.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            return dateInSummary.contains(expectedStr) || dateInSummary.contains(expectedStr2);
        } catch (Exception e) {
            return false;
        }
    }
}
