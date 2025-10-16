package stepDefinitions;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Hooks {
    public static WebDriver driver;

    @Before
    public void setup() {
        // setup chromedriver automatically
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        // mobile-like size/emulation: using fixed window size for reliability
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-notifications");
        // headless is optional
        // options.addArguments("--headless=new");

        driver = new ChromeDriver(options);
        // set mobile-ish window (or use real device emulation if desired)
        driver.manage().window().setSize(new Dimension(390, 844)); // typical mobile portrait
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
