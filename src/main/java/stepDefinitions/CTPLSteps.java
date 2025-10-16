package stepDefinitions;

import io.cucumber.java.en.*;
import pages.CTPLHomePage;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import pages.CTPLHomePage;
import pages.CTPLPlanPage;
import pages.CTPLCheckoutPage;

public class CTPLSteps {
    WebDriver driver = Hooks.driver;
    CTPLHomePage homePage;
    CTPLPlanPage planPage;
    CTPLCheckoutPage checkoutPage;

    @Given("the CTPL homepage is opened in mobile view")
    public void openHome() {
        homePage = new CTPLHomePage(driver);
        homePage.navigateToCTPLPage();
    }

    // Requirement 1
    @When("I read prices from all plan cards")
    public void readCardPrices() {
        homePage.captureCardPrices();
    }

    @When("I read prices from the comparison table")
    public void readTablePrices() {
        homePage.captureTablePrices();
    }

    @Then("the card prices must match table prices")
    public void comparePrices() {
        Assert.assertTrue(homePage.compareCardAndTablePrices(), "Card and table prices mismatch");
    }

    // Requirement 2 - select random
    @When("I select a random plan")
    public void selectRandomPlan() {
        planPage = homePage.selectRandomPlan();
    }

    @Then("I proceed to checkout page")
    public void proceedToCheckout() {
        // assume clicking a "select" navigates to a page with /checkout/payment
        checkoutPage = planPage.proceedToCheckout();
    }

    @Then("current URL should contain \"/checkout/payment\"")
    public void urlShouldContainCheckout() {
        Assert.assertTrue(driver.getCurrentUrl().contains("/checkout/payment"),
                "URL does not contain /checkout/payment. Current: " + driver.getCurrentUrl());
    }

    // Requirement 2 - select by name
    @When("I select the plan {string}")
    public void selectPlanByName(String planName) {
        planPage = homePage.selectPlanByName(planName);
    }

    // Requirement 4 - summary validations and date changes
    @Then("I validate checkout summary details")
    public void validateSummary() {
        Assert.assertTrue(checkoutPage.verifySummaryDetails(), "Summary details verification failed");
    }

    @When("I change policy start date to maximum allowed")
    public void changeToMax() {
        checkoutPage.changePolicyStartDateToMax();
    }

    @Then("the summary shows the maximum date as selected")
    public void verifyMaxDate() {
        Assert.assertTrue(checkoutPage.verifyPolicyStartDateChangedToMax(), "Policy start date did not update to max");
    }

    @When("I change policy start date to minimum allowed")
    public void changeToMin() {
        checkoutPage.changePolicyStartDateToMin();
    }

    @Then("the summary shows the minimum date as selected")
    public void verifyMinDate() {
        Assert.assertTrue(checkoutPage.verifyPolicyStartDateChangedToMin(), "Policy start date did not update to min");
    }
}
