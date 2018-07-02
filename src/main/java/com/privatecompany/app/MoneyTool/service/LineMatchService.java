package com.privatecompany.app.MoneyTool.service;

import com.privatecompany.app.MoneyTool.entity.Match;
import org.jsoup.Jsoup;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.jsoup.nodes.Document;

import java.util.List;
import org.slf4j.Logger;

@Service
@PropertySource("classpath:links.properties")
public class LineMatchService implements MatchService {

    private final Environment env;

    private final WebDriver driver;

    private static Logger log = LoggerFactory.getLogger(LineMatchService.class);

    @Autowired
    public LineMatchService(WebDriver driver, Environment env){
        this.driver=driver;
        this.env = env;
    }

    @Override
    public List<Match> getMatches() {
        log.debug("Try to get url for driver");
        driver.get(env.getProperty("1xbet.url"));

        log.debug("Try to parse driverPage");
        Document doc = Jsoup.parse(driver.getPageSource());
        System.out.println(doc);
        return null;
    }
}
