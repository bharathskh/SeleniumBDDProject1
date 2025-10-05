package org.example.stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.example.pages.LoginPage;
import org.testng.Assert;
import org.openqa.selenium.WebDriver;
import org.example.core.DriverFactory;
import org.example.core.ConfigManager;

public class LoginSteps {
    private final LoginPage loginPage = new LoginPage();
    private final WebDriver driver = DriverFactory.getDriver();

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        // Fixed typo and use ConfigManager for dynamic config
        String baseUrl = ConfigManager.get("base.url");
        driver.get(baseUrl);
    }

    @When("I enter valid username and password")
    public void i_enter_valid_username_and_password() {
        loginPage.enterUsername("testuser");
        loginPage.enterPassword("password123");
        System.out.println("in login page");
    }

    @When("I click the login button")
    public void i_click_the_login_button() {
        loginPage.clickLogin();
        System.out.println("in login page");
    }

    @Then("I should be redirected to the dashboard")
    public void i_should_be_redirected_to_the_dashboard() {
        Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"), "Not redirected to dashboard");
    }
}
