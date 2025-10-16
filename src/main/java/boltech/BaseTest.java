package boltech;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import io.github.bonigarcia.wdm.WebDriverManager;

public abstract class BaseTest {
	
	 protected WebDriver driver;
	 protected WebDriverWait wait;

	 @BeforeMethod
	    public void setup() {
	        WebDriverManager.chromedriver().setup(); // Manages ChromeDriver setup
	        
	        Map<String, String> mobileEmulation = new HashMap<>();
	        mobileEmulation.put("deviceName", "iPhone 12 Pro"); // Or any other valid device name

	        // Create ChromeOptions and set the experimental option for mobile emulation
	        ChromeOptions chromeOptions = new ChromeOptions();
	        chromeOptions.setExperimentalOption("mobileEmulation", mobileEmulation);

	        // Initialize ChromeDriver with the configured ChromeOptions
	        driver = new ChromeDriver(chromeOptions);
	        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
//	        driver.manage().window().setSize(new org.openqa.selenium.Dimension(450, 900));
//	        driver.manage().window().setPosition(new org.openqa.selenium.Point(500, 100));
	    }

	 @AfterMethod
	    public void tearDown() {
	        if (driver != null) {
	            driver.quit();
	        }
	    }
	 public void waits(String xpath) {
			        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
	 }
}
