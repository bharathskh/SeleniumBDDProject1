package org.example.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.core.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Hooks {
    private static final Logger logger = LogManager.getLogger(Hooks.class);
    private WebDriver driver;

    @Before
    public void setUp(Scenario scenario) {
        String browser = System.getProperty("browser", "chrome");
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));
        DriverFactory.initDriver(browser, headless);
        driver = DriverFactory.getDriver();
        logger.info("Starting scenario: {}", scenario.getName());
    }

    @After
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed()) {
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            scenario.attach(screenshot, "image/png", "Failure Screenshot");
            logger.error("Scenario failed: {}. Screenshot attached.", scenario.getName());
        }
        // Attach healing logs if available
        try {
            String healingLogPath = "reports/logs/healing.log";
            if (Files.exists(Paths.get(healingLogPath))) {
                byte[] healingLog = Files.readAllBytes(Paths.get(healingLogPath));
                scenario.attach(healingLog, "text/plain", "Healing Log");
            }
        } catch (Exception e) {
            logger.warn("Could not attach healing log: {}", e.getMessage());
        }
        DriverFactory.quitDriver();
        logger.info("Scenario finished: {}", scenario.getName());
    }
}

