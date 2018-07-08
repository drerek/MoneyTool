package com.privatecompany.app.MoneyTool.service;

import com.privatecompany.app.MoneyTool.entity.Command;
import com.privatecompany.app.MoneyTool.entity.Match;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service
public class LiveMatchService implements MatchService {

    private final Environment env;

    private final WebDriver driver;

    private static Logger log = LoggerFactory.getLogger(LiveMatchService.class);

    @Autowired
    public LiveMatchService(Environment env, WebDriver driver) {
        this.env = env;
        this.driver = driver;
    }

    @Override
    public List<Command> getCommands() {
        log.debug("Try to get url for driver");
        driver.get(env.getProperty("flashscore.url"));

        log.debug("Try to click live games");
        driver.findElement(By.linkText("LIVE Games")).click();

        log.debug("Try to parse driverPage");
        Document doc = Jsoup.parse(driver.getPageSource());

        log.debug("Try to get names of commands");
        Elements namesHome = doc.select("span.padl");
        Elements namesAway = doc.select("span.padr");

        List<Command> commandNames = new LinkedList<>();
        for (int i=0;i<namesAway.size();i++){
            commandNames.add(new Command(namesHome.get(i).text().trim()));
            commandNames.add(new Command(namesAway.get(i).text().trim()));
        }

        return commandNames;
    }

    public List<Match> getMatches() {
        log.debug("Try to get url for driver");
        driver.get(env.getProperty("flashscore.url"));

        log.debug("Try to get screenshot");
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
// Now you can do whatever you need to do with it, for example copy somewhere

        try {
            FileUtils.copyFile(scrFile, new File("C:\\screens\\flashscorelive.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        log.debug("Try to click live games");
        driver.findElement(By.linkText("LIVE Games")).click();

        log.debug("Try to parse driverPage");
        Document doc = Jsoup.parse(driver.getPageSource());

        log.debug("Try to get names of commands");
        Elements namesHome = doc.select("span.padr");
        Elements namesAway = doc.select("span.padl");

        log.debug("Try to get time");
        Elements time = doc.select("td.cell_ad");

        List<Match> matches = new LinkedList<>();
        for (int i=0;i<namesAway.size();i++){
            matches.add(new Match(new Command(namesHome.get(i).text().trim()),
                    new Command(namesAway.get(i).text().trim()),
                    time.get(i).text().trim()));
        }

        return matches;
    }

}
