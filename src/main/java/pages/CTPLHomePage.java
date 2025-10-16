package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import boltech.BaseTest;

import java.time.Duration;
import java.util.*;

public class CTPLHomePage {
    private WebDriver driver;
    private WebDriverWait wait;
    private List<String> cardPrices = new ArrayList<>();
    private List<String> tablePrices = new ArrayList<>();

    private final String CTPL_URL = "https://www.bolttech.co.th/en/fwd/car-insurance/ctpl-insurance?utm_source=fwd&utm_medium=genesis";

    public CTPLHomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
    }

    public void navigateToCTPLPage() {
        driver.get(CTPL_URL);
        // wait for page main component or plan cards to load
        wait.until(ExpectedConditions.or(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("fwd-plan-card")),
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("fwd-plan-table"))
        ));
    }

    // Try to get shadow root via Selenium API; fallback to JS
    private SearchContext getShadowRoot(WebElement host) {
        try {
            // Selenium 4+ supports getShadowRoot()
            return host.getShadowRoot();
        } catch (Throwable t) {
            return (SearchContext) ((JavascriptExecutor) driver)
                    .executeScript("return arguments[0].shadowRoot", host);
        }
    }

    public void captureCardPrices() {
        cardPrices.clear();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("fwd-plan-card")));
        List<WebElement> cards = driver.findElements(By.cssSelector("fwd-plan-card"));

        for (WebElement card : cards) {
            try {
                SearchContext shadow = getShadowRoot(card);
                // selectors inside the shadow (best effort)
                List<By> trySelectors = Arrays.asList(
                        By.cssSelector(".plan__price"),
                        By.cssSelector(".plan-price"),
                        By.cssSelector(".price"),
                        By.cssSelector(".text-green"),
                        By.cssSelector(".price-amount")
                );
                String priceText = null;
                for (By s : trySelectors) {
                    try {
                        WebElement el = shadow.findElement(s);
                        priceText = el.getText();
                        if (priceText != null && !priceText.trim().isEmpty()) break;
                    } catch (Exception ignored) {}
                }
                if (priceText == null) priceText = "0";
                priceText = normalizePrice(priceText);
                cardPrices.add(priceText);
            } catch (Exception e) {
                cardPrices.add("0");
            }
        }
        System.out.println("Card Prices: " + cardPrices);
    }

    public void captureTablePrices() {
        tablePrices.clear();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("fwd-plan-table")));
        WebElement tableHost = driver.findElement(By.cssSelector("fwd-plan-table"));
        SearchContext tableShadow = getShadowRoot(tableHost);

        // find rows/prices inside table shadow
        List<By> trySelectors = Arrays.asList(
                By.cssSelector(".plan-price"),
                By.cssSelector(".text-green"),
                By.cssSelector(".price"),
                By.cssSelector(".price-amount"),
                By.cssSelector("td.price")
        );

        List<WebElement> priceEls = new ArrayList<>();
        for (By s : trySelectors) {
            try {
                priceEls = tableShadow.findElements(s);
                if (!priceEls.isEmpty()) break;
            } catch (Exception ignored) {}
        }

        if (priceEls.isEmpty()) {
            // fallback: gather all text and try to extract numbers line by line
            String all = tableShadow.toString();
            // safe fallback: leave tablePrices empty
        } else {
            for (WebElement el : priceEls) {
                try {
                    String t = el.getText();
                    tablePrices.add(normalizePrice(t));
                } catch (Exception ignored) {
                    tablePrices.add("0");
                }
            }
        }
        System.out.println("Table Prices: " + tablePrices);
    }

    private String normalizePrice(String raw) {
        if (raw == null) return "0";
        String cleaned = raw.replaceAll("[^0-9]", "");
        return cleaned.isEmpty() ? "0" : cleaned;
    }

    public boolean compareCardAndTablePrices() {
        if (cardPrices.size() != tablePrices.size()) {
            System.out.println("Different counts - cards: " + cardPrices.size() + " table: " + tablePrices.size());
            return false;
        }
        for (int i = 0; i < cardPrices.size(); i++) {
            if (!cardPrices.get(i).equals(tablePrices.get(i))) {
                System.out.println("Mismatch at index " + i + " card:" + cardPrices.get(i) + " table:" + tablePrices.get(i));
                return false;
            }
        }
        System.out.println("All card prices match table prices");
        return true;
    }

    // Select a random plan: clicks first available "select" button inside the chosen card.
    public CTPLPlanPage selectRandomPlan() {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("fwd-plan-card")));
        List<WebElement> cards = driver.findElements(By.cssSelector("fwd-plan-card"));
        if (cards.isEmpty()) throw new RuntimeException("No plan cards found");
        Random r = new Random();
        WebElement chosen = cards.get(r.nextInt(cards.size()));

        // enter shadow and click the primary action (Select / Buy)
        try {
            SearchContext shadow = getShadowRoot(chosen);
            List<By> actionSelectors = Arrays.asList(
                    By.cssSelector("button.primary"),
                    By.cssSelector("button.btn-select"),
                    By.cssSelector("button")
            );
            boolean clicked = false;
            for (By s : actionSelectors) {
                try {
                    WebElement btn = shadow.findElement(s);
                    if (btn.isDisplayed()) { btn.click(); clicked = true; break; }
                } catch (Exception ignored) {}
            }
            if (!clicked) {
                // fallback: click host element
                chosen.click();
            }
        } catch (Exception e) {
            chosen.click();
        }
        return new CTPLPlanPage(driver);
    }

    // Select a plan by exact/partial product name visible on the card
    public CTPLPlanPage selectPlanByName(String name) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("fwd-plan-card")));
        List<WebElement> cards = driver.findElements(By.cssSelector("fwd-plan-card"));
        for (WebElement card : cards) {
            try {
                SearchContext shadow = getShadowRoot(card);
                // Try to find title/name
                List<By> titleSelectors = Arrays.asList(
                    By.cssSelector(".plan-title"),
                    By.cssSelector(".title"),
                    By.cssSelector(".card-title")
                );
                String titleText = "";
                for (By sel : titleSelectors) {
                    try {
                        WebElement t = shadow.findElement(sel);
                        titleText = t.getText();
                        if (titleText != null && !titleText.isEmpty()) break;
                    } catch (Exception ignored){}
                }
                if (titleText != null && titleText.toLowerCase().contains(name.toLowerCase())) {
                    // click select inside this card
                    List<By> actionSelectors = Arrays.asList(
                            By.cssSelector("button.primary"),
                            By.cssSelector("button.btn-select"),
                            By.cssSelector("button")
                    );
                    boolean clicked = false;
                    for (By s : actionSelectors) {
                        try {
                            WebElement btn = shadow.findElement(s);
                            if (btn.isDisplayed()) { btn.click(); clicked = true; break; }
                        } catch (Exception ignored) {}
                    }
                    if (!clicked) card.click();
                    return new CTPLPlanPage(driver);
                }
            } catch (Exception ignored) {}
        }
        throw new RuntimeException("Plan with name '" + name + "' not found");
    }
}
