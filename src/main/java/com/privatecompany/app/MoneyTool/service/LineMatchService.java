package com.privatecompany.app.MoneyTool.service;

import com.privatecompany.app.MoneyTool.entity.Command;
import com.privatecompany.app.MoneyTool.entity.Match;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
@PropertySource("classpath:links.properties")
public class LineMatchService {

    private final Environment env;

    private final WebDriver driver;

    private static Logger log = LoggerFactory.getLogger(LineMatchService.class);

    @Autowired
    public LineMatchService(WebDriver driver, Environment env) {
        this.driver = driver;
        this.env = env;
    }

    List<Match> getLiveResultMatches(String url) {
        log.debug("Try to get url for driver");
        driver.get(url);

        log.debug("Try to parse driverPage");
        Document doc = Jsoup.parse(driver.getPageSource());

        Elements elements = doc.select("a.matches-list-match");

        List<Match> list = new ArrayList<>();
        for (Element element : elements) {
            String name1 = element.select("span.team1>span").text();
            String name2 = element.select("span.team2>span").text();
            String date = element.select("span.match-time-date").text();
            String time = element.select("span.match-time-time").text();

            list.add(new Match(new Command(name1), new Command(name2), date + " " + time));
        }

        return list;
    }

    public List<Match> getMatchesFlashScore(String url) {
        log.debug("Try to get url for driver");
        driver.get(url);

        log.debug("Try to click scheduled games");
        try {
            driver.findElement(By.linkText("Scheduled")).click();
        } catch (NoSuchElementException e) {
            log.error("No element found");
        }

        log.debug("Try to get screenshot");
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File("C:\\screens\\flashscoreline.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        log.debug("Try to parse driverPage");
        Document doc = Jsoup.parse(driver.getPageSource());

        log.debug("Try to get names of commands");
        Elements namesHome;
        Elements namesAway;

        if (url.equals(env.getProperty("flashscorehockey.url"))) {
            namesHome = doc.select("div.event__match.event__match--scheduled.event__match--twoLine>div.event__participant.event__participant--home");
            namesAway = doc.select("div.event__match.event__match--scheduled.event__match--twoLine>div.event__participant.event__participant--away");
        } else {
            namesHome = doc.select("div.event__match.event__match--scheduled.event__match--oneLine>div.event__participant.event__participant--home");
            namesAway = doc.select("div.event__match.event__match--scheduled.event__match--oneLine>div.event__participant.event__participant--away");
        }


        log.debug("Try to get time");
        Elements time = doc.select("div.event__time");

        List<Match> matches = new LinkedList<>();
        if (namesHome == null) return matches;
		if (namesAway.size() != namesHome.size() || namesAway.size() != time.size()){
			matches.add(new Match(new Command("time not correlated"),
			new Command("time not correlated"), ""));
			return matches;
			} 
			
        for (int i = 0; i < namesAway.size(); i++) {
            String oneTime = time.get(i).text().trim();
            if (oneTime.contains("FRO")){
                oneTime = oneTime.substring(0, oneTime.indexOf("FRO")-1);
            }
            matches.add(new Match(new Command(namesHome.get(i).text().trim()),
                    new Command(namesAway.get(i).text().trim()),
                    oneTime));
        }

        return matches;

    }

    List<Match> getMatches1xbetv2(String url) {
        String urlTemplate = "https://1xbetua.com/en/";

        log.debug("Try to get url for driver");
        driver.get(url);

        log.debug("Try to get screenshot");
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        try {
            FileUtils.copyFile(scrFile, new File("C:\\screens\\xbetscreen.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.debug("Try to parse driverPage");
        Document doc = Jsoup.parse(driver.getPageSource());

        Elements leagues = doc.select("ul.liga_menu");
        Elements leaguesChild = new Elements();
        log.debug("Try to get divs for games");
        for (Element element : leagues) {
            if (element.childNodeSize() != 0) {
                leaguesChild.addAll(element.children());
            }
        }
        List<String> links = new LinkedList<>();
        for (Element element : leaguesChild) {
            links.add(element.select("a").attr("href"));
        }


        List<Match> matches = new LinkedList<>();
        LocalDate localDate = LocalDate.now();
        log.info("localDate.getDayOfMonth " + localDate.getDayOfMonth());
        //log.info(localDate.format(ISO_LOCAL_DATE).toString());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        log.info("links count " + links.size());
        int totalMatches = 0;
        for (String link : links) {
            driver.get(urlTemplate + link);
            Document document = Jsoup.parse(driver.getPageSource());
            Elements matchesElem = document.select("div.nameCon");
            totalMatches += matchesElem.size();
            log.info("link:" + urlTemplate + link + " Elements count " + matchesElem.size());
            for (Element match : matchesElem) {
                String data = match.select("div.date").select("span").text();
                String date = data.substring(0, data.indexOf(" "));
                String time = data.substring(data.indexOf(" ") + 1);
                String commands = match.select("span.n").text();
                String homeCommand = commands.substring(0, commands.indexOf(" - "));
                String awayCommand = commands.substring(commands.indexOf(" - ") + 3);

                //log.info("LocalDate.parse(date,formatter).getDayOfMonth()="+LocalDate.parse(date,formatter).getDayOfMonth());
                if (localDate.getDayOfMonth() == LocalDate.parse(date, formatter).getDayOfMonth()) {
                    matches.add(new Match(new Command(homeCommand), new Command(awayCommand), time));
                }

            }

        }
        log.info("total matches count=" + totalMatches);
        log.info("matches count=" + matches.size());

        return matches;

    }

    public List<Match> getMatchesPariMatch(String url, String tag) {
        String urlTemplate = "https://www.parimatch.com/en/";
        log.debug("Try to get url for driver");
        driver.get(urlTemplate);

        if (tag.equals("Football")) {
            log.debug("Try to click on Football button");
            WebElement checkbox = driver.findElement(By.linkText("Football"));
            checkbox.findElement(By.cssSelector("em")).click();
        }
        else if(tag.equals("Hockey")) {
            log.debug("Try to click on Hockey button");
            WebElement checkbox = driver.findElement(By.linkText("Ice Hockey"));
            checkbox.findElement(By.cssSelector("em")).click();
        }
        else {
            log.error("Tag is not of all ifs");
            return null;
        }

        // Click on show button
        log.debug("Try to click on show button");
        driver.findElement(By.id("buttons")).findElements(By.cssSelector("button")).get(1).click();

        log.debug("Try to get screenshot");
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        try {
            FileUtils.copyFile(scrFile, new File("C:\\screens\\parimatch.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        final Wait<WebDriver> wait = new WebDriverWait(driver, 20, 1000);
        wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.cssSelector("tbody.row1.processed"))));

        log.debug("Try to get PageSource");
        String pageSource;
        try{
            pageSource = driver.getPageSource();
        }
        catch (Exception e){
            log.error("Exception:"+e.getMessage());
//            driver.close();
            return null;
        }

        Document doc = Jsoup.parse(pageSource);
        Elements leagues = doc.select("tbody.row1.processed");
        leagues.addAll(doc.select("tbody.row2.processed"));

        log.debug("leagues=" + leagues);

        List<Match> matches = new LinkedList<>();
        LocalDate localDate = LocalDate.now();
        log.info("localDate.getDayOfMonth " + localDate.getDayOfMonth());
        //log.info(localDate.format(ISO_LOCAL_DATE).toString());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        log.info("links count " + leagues.size());
        int totalMatches = 0;
        for (Element match : leagues) {
            log.debug("League=" + match.text());
            try {
                Elements tds = match.select("td");

                String data = tds.get(1).text().trim();
                String commandNames = tds.get(2).html().trim();
                if (data.equals("") || commandNames.equals("") || commandNames.contains("</span>")) {

                    continue;
                }

                String date = data.substring(0, data.indexOf(" "));
                String time = data.substring(data.indexOf(" ") + 1);

                String homeCommand = commandNames.substring(commandNames.indexOf(">") + 1, commandNames.indexOf("<br>"));
                String awayCommand = commandNames.substring(commandNames.indexOf("<br>") + 4, commandNames.indexOf("</a>"));

                if (homeCommand.equals("") || awayCommand.equals("")) {
                    continue;
                }
                if (homeCommand.contains("<small>")) {
                    homeCommand = homeCommand.substring(homeCommand.indexOf("<small>") + "<small>".length(), homeCommand.indexOf("</small>"));
                }
                if (awayCommand.contains("<small>")) {
                    awayCommand = awayCommand.substring(awayCommand.indexOf("<small>") + "<small>".length(), awayCommand.indexOf("</small>"));
                }
//                log.info("date=" + date + " time=" + time + " homeCommand=" + homeCommand + " awayCommand=" + awayCommand);
                if (localDate.getDayOfMonth() == Integer.valueOf(data.substring(0, data.indexOf("/")))) {
                    matches.add(new Match(new Command(homeCommand), new Command(awayCommand), time));
                }
                totalMatches++;
            } catch (Exception e) {
                continue;
            }
        }
        log.info("total matches count=" + totalMatches);
        log.info("matches count=" + matches.size());

        return matches;

    }

}
