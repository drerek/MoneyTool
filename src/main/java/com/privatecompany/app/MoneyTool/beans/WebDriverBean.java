package com.privatecompany.app.MoneyTool.beans;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebDriverBean {

    @Bean
    public WebDriver getWebDriver(){
        return new PhantomJSDriver();
    }
}
