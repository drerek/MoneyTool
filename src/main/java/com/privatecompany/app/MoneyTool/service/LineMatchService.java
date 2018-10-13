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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

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
    public List<Command> getCommands() {
        log.debug("Try to get url for driver");
        driver.get(env.getProperty("1xbet.url"));

        log.debug("Try to parse driverPage");
        Document doc = Jsoup.parse(driver.getPageSource());

        log.debug("Try to get elements");
        Elements names = doc.select("span.c-events__team");


        List<Command> commandNames = new LinkedList<>();
        log.debug("Adding commandNames to list");
        for (Element element: names){
            commandNames.add(new Command(element.text().trim()));
        }

        return commandNames;
    }

    public List<Match> getMatchesFlashScore(){
        log.debug("Try to get url for driver");
        driver.get(env.getProperty("flashscore.url"));

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

    public List<Match> getMatches1xbet(){
        log.debug("Try to get url for driver");
        driver.get(env.getProperty("1xbet.url"));

        log.debug("Try to get screenshot");
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);

        try {
            FileUtils.copyFile(scrFile, new File("C:\\screens\\xbetscreen.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        log.debug("Try to click top 100");
//        driver.findElement(By.linkText("TOP 100"));

        log.debug("Try to parse driverPage");
        Document doc = Jsoup.parse(driver.getPageSource());

        log.debug("Try to get divs for games");
        Elements divs = doc.select("div.c-events__item.c-events__item_game");
        Elements names = new Elements();
        Elements times = new Elements();
        for (Element div: divs){
                Elements spans = div.select("span.c-events__team");
                if (spans.size()==2) {
                    names.addAll(spans);
                    times.add(div.select("div.c-events__time").first());
                }
        }


        List<Command> commandNames = new LinkedList<>();
        log.debug("Adding commandNames to list");
        for (Element element: names){
            commandNames.add(new Command(element.text().trim()));
        }

        log.debug("Adding matches to list");
        int j=0;
        List<Match> matches = new LinkedList<>();
        for (int i=0;i<commandNames.size();i=i+2){
            matches.add(new Match(commandNames.get(i),
                    commandNames.get(i+1),
                    times.get(j).text().trim().substring(5).trim()));
            j++;
        }

        return matches;

    }

    public List<Match> getMatches1xbetv2(){
        String urlTemplate="https://1xbetua.com/en/";
        log.debug("Try to get url for driver");
        driver.get("https://1xbetua.com/en/betexchange/Football/");

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
