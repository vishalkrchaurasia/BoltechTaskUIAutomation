package boltech;

import org.testng.annotations.Test;

public class MySeleniumTest extends BaseTest {

    @Test
    public void verifyGoogleTitle() {
        driver.get(ConfigReader.get("base.url"));
        String actualTitle = driver.getTitle();
        String expectedTitle = "Compulsory Car Insurance (CTPL) Online | bolttech.co.th";
        assert actualTitle.equals(expectedTitle) : "Title mismatch: Expected " + expectedTitle + " but got " + actualTitle;
    }
}