package com.privatecompany.app.MoneyTool.service;

import com.privatecompany.app.MoneyTool.entity.Command;
import com.privatecompany.app.MoneyTool.entity.Match;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
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
import java.util.LinkedList;
import java.util.List;

@Service
@PropertySource("classpath:links.properties")
public class LineMatchService {

    private final Environment env;

    private final WebDriver driver;

    private static Logger log = LoggerFactory.getLogger(LineMatchService.class);

    @Autowired
    public LineMatchService(WebDriver driver, Environment env){
        this.driver=driver;
        this.env = env;
    }

    List<Match> getMatchesFlashScore(String url){
        log.debug("Try to get url for driver");
        driver.get(url);

        log.debug("Try to click scheduled games");
        driver.findElement(By.linkText("Scheduled")).click();

        log.debug("Try to get screenshot");
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File("C:\\screens\\flashscoreline.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        log.debug("Try to parse driverPage");
        Document doc = Jsoup.parse(driver.getPageSource());

        log.debug("Try to get names of commands");
        Elements namesHome = null;
        Elements namesAway = null;
        if (url.equals(env.getProperty("flashscore.url"))) {
            namesHome = doc.select("span.padr");
            namesAway = doc.select("span.padl");
        }
        else if(url.equals(env.getProperty("flashscorehockey.url"))){
            namesHome = doc.select("td.cell_ab.team-home>span.padl");
            namesAway = doc.select("td.cell_ac.team-away>span.padl");
        }

        log.debug("Try to get time");
        Elements time = doc.select("td.cell_ad");

        List<Match> matches = new LinkedList<>();
        if (namesHome==null)return matches;
        for (int i=0;i<namesAway.size();i++){
            matches.add(new Match(new Command(namesHome.get(i).text().trim()),
                    new Command(namesAway.get(i).text().trim()),
                    time.get(i).text().trim()));
        }

        return matches;

    }

    public List<Match> getMatches1xbetv2(String url){
        String urlTemplate="https://1xbetua.com/en/";

        log.debug("Try to get url for driver");
        driver.get(url);

        log.debug("Try to get screenshot");
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

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
        for (Element element: leagues){
            if (element.childNodeSize() != 0) {
                leaguesChild.addAll(element.children());
            }
        }
        List<String> links = new LinkedList<>();
        for (Element element: leaguesChild){
         links.add(element.select("a").attr("href"));
        }


        List<Match> matches = new LinkedList<>();
        LocalDate localDate = LocalDate.now();
        log.info("localDate.getDayOfMonth "+localDate.getDayOfMonth());
        //log.info(localDate.format(ISO_LOCAL_DATE).toString());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        log.info("links count "+links.size());
        int totalMatches = 0;
        for (String link: links){
            driver.get(urlTemplate+link);
            Document document = Jsoup.parse(driver.getPageSource());
            Elements matchesElem = document.select("div.nameCon");
            totalMatches+=matchesElem.size();
            log.info("link:"+urlTemplate+link+" Elements count "+matchesElem.size());
            for (Element match: matchesElem){
                String data = match.select("div.date").select("span").text();
                String date = data.substring(0,data.indexOf(" "));
                String time = data.substring(data.indexOf(" ")+1,data.length());
                String commands = match.select("span.n").text();
                String homeCommand = commands.substring(0,commands.indexOf(" - "));
                String awayCommand = commands.substring(commands.indexOf(" - ")+3,commands.length());

                //log.info("LocalDate.parse(date,formatter).getDayOfMonth()="+LocalDate.parse(date,formatter).getDayOfMonth());
                if (localDate.getDayOfMonth() == LocalDate.parse(date,formatter).getDayOfMonth()){
                    matches.add(new Match(new Command(homeCommand),new Command(awayCommand),time));
                }

            }

        }
        log.info("total matches count="+totalMatches);
        log.info("matches count="+matches.size());
//        for (Match match: matches){
//            System.out.println(match);
//        }
        return matches;

    }
}
