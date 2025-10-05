package org.example.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.example.pages.LoginPage;
import org.testng.Assert;
import org.openqa.selenium.WebDriver;
import org.example.core.DriverFactory;

public class LoginSteps {
    private final LoginPage loginPage = new LoginPage();
    private final WebDriver driver = DriverFactory.getDriver();

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
<<<<<<< HEAD
        System.out.ptintln("");
=======
        System.out.println("Print");
>>>>>>> 027c271 (Update LoginSteps.java and synchronize with framework best practices)
        driver.get(System.getProperty("base.url", "https://example.com"));
    }

    @When("I enter valid username and password")
    public void i_enter_valid_username_and_password() {
        loginPage.enterUsername("testuser");
        loginPage.enterPassword("password123");
    }

    @When("I click the login button")
    public void i_click_the_login_button() {
        loginPage.clickLogin();
    }

    @Then("I should be redirected to the dashboard")
    public void i_should_be_redirected_to_the_dashboard() {
        Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"), "Not redirected to dashboard");
    }
}

