package com.privatecompany.app.MoneyTool.beans;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Configuration
@PropertySource("classpath:links.properties")
public class WebDriverBean {
    private final Environment env;

    public WebDriverBean(Environment env) {
        this.env = env;
    }

    @Bean
    public WebDriver getWebDriver(){
        System.setProperty("webdriver.chrome.driver", Objects.requireNonNull(env.getProperty("path.chromedriver")));
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setBinary(Objects.requireNonNull(env.getProperty("path.chrome")));
        chromeOptions.addArguments("--headless");
        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        return driver;
        //return new PhantomJSDriver();
    }
}
