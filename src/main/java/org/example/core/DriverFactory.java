package org.example.core;

import com.epam.healenium.SelfHealingDriver;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.net.MalformedURLException;
import java.util.Objects;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final Logger logger = LogManager.getLogger(DriverFactory.class);

    public static WebDriver getDriver() {
        if (driver.get() == null) {
            driver.set(initDriver());
        }
        return driver.get();
    }

    private static WebDriver initDriver() {
        String browser = ConfigManager.get("browser");
        boolean isHeadless = ConfigManager.getBoolean("headless", false);
        String gridUrl = ConfigManager.get("selenium.grid.url");
        String healeniumProxyUrl = ConfigManager.get("healenium.proxy.url");
        WebDriver rawDriver;
        try {
            if (gridUrl != null && !gridUrl.isEmpty()) {
                // Remote execution (Selenium Grid)
                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setBrowserName(browser);
                if (browser.equals("chrome")) {
                    ChromeOptions options = new ChromeOptions();
                    if (isHeadless) options.addArguments("--headless=new");
                    options.addArguments("--window-size=1920,1080");
                    capabilities.merge(options);
                } else if (browser.equals("firefox")) {
                    FirefoxOptions options = new FirefoxOptions();
                    if (isHeadless) options.addArguments("-headless");
                    capabilities.merge(options);
                } else if (browser.equals("edge")) {
                    EdgeOptions options = new EdgeOptions();
                    if (isHeadless) options.addArguments("--headless=new");
                    capabilities.merge(options);
                }
                rawDriver = new RemoteWebDriver(new URL(gridUrl), capabilities);
            } else {
                // Local execution
                switch (browser) {
                    case "firefox":
                        WebDriverManager.firefoxdriver().setup();
                        FirefoxOptions ffOptions = new FirefoxOptions();
                        if (isHeadless) ffOptions.addArguments("-headless");
                        rawDriver = new FirefoxDriver(ffOptions);
                        break;
                    case "edge":
                        WebDriverManager.edgedriver().setup();
                        EdgeOptions edgeOptions = new EdgeOptions();
                        if (isHeadless) edgeOptions.addArguments("--headless=new");
                        rawDriver = new EdgeDriver(edgeOptions);
                        break;
                    case "chrome":
                    default:
                        WebDriverManager.chromedriver().setup();
                        ChromeOptions chromeOptions = new ChromeOptions();
                        if (isHeadless) chromeOptions.addArguments("--headless=new");
                        chromeOptions.addArguments("--window-size=1920,1080");
                        rawDriver = new ChromeDriver(chromeOptions);
                        break;
                }
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Selenium Grid URL", e);
        }
        // Wrap with Healenium SelfHealingDriver if proxy URL is set
        if (healeniumProxyUrl != null && !healeniumProxyUrl.isEmpty()) {
            System.setProperty("healenium.proxy.host", healeniumProxyUrl.replace("http://", "").replace(":7878", ""));
            System.setProperty("healenium.proxy.port", "7878");
        }
        WebDriver healingDriver = SelfHealingDriver.create(rawDriver);
        logger.info("Initialized {} browser with headless={} and Healenium", browser, isHeadless);
        return healingDriver;
    }

    public static void quitDriver() {
        WebDriver drv = driver.get();
        if (Objects.nonNull(drv)) {
            drv.quit();
            driver.remove();
            logger.info("WebDriver instance quit and removed from ThreadLocal");
        }
    }
}
