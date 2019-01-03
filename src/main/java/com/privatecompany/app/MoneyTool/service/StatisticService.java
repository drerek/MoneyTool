package com.privatecompany.app.MoneyTool.service;

import com.privatecompany.app.MoneyTool.entity.CommandStat;
import com.privatecompany.app.MoneyTool.entity.Match;
import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

import static java.lang.Thread.sleep;

@Service
@PropertySource("classpath:links.properties")
public class StatisticService {
    private final Environment env;

    private final LineMatchService lineMatchService;

    private final MailService mailService;

    private final WebDriver driver;

    private static Logger log = LoggerFactory.getLogger(LineMatchService.class);

    @Autowired
    public StatisticService(WebDriver driver, Environment env, LineMatchService lineMatchService, MailService mailService) {
        this.driver = driver;
        this.env = env;
        this.lineMatchService = lineMatchService;
        this.mailService = mailService;
    }

    private static boolean isClickable(By webe, WebDriver driver) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.elementToBeClickable(webe));

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getAllLinks(String url) {
        driver.get(url);
        log.info("Try to open full page");
        while (isClickable(By.linkText("Show more matches"), driver)) {
            driver.findElement(By.linkText("Show more matches")).click();
            log.info("Clicking");
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        log.debug("Try to get screenshot");
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File("C:\\screens\\flashscore" + System.currentTimeMillis() + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        log.debug("Try to parse driverPage");
        Document doc = Jsoup.parse(driver.getPageSource());

        Elements elements = doc.select("tr.stage-finished");

        List<String> ids = new LinkedList<>();
        for (Element element : elements) {
            ids.add((element.select("tr").attr("id")).substring(4));
        }

        return ids;
    }

    public List<String> getAllLinksLiveResult(String url) {
        driver.get(url);

        log.debug("Try to parse driverPage");
        Document doc = Jsoup.parse(driver.getPageSource());

        Elements elements = doc.select("a.matches-list-match");

        List<String> ids = new LinkedList<>();
        for (Element element : elements) {
            ids.add((element.select("a").attr("href")).replace("/matches/", "/txt/"));
        }

        return ids;
    }

    public void collectAllMatches(String url, String fileName) {
        List<String> ids = getAllLinks(url);
        final String template = "https://www.flashscore.com/match/";
        try (FileWriter writer = new FileWriter(new File(fileName), true)) {
            for (String id : ids) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("Try to get" + template + id);
                driver.get(template + id + "/#live-commentary;0");

                //driver.findElement(By.id("li-match-commentary")).click();

                log.debug("Try to get screenshot");
                File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
//                try {
//                    FileUtils.copyFile(scrFile, new File("C:\\screens\\flashscore" + System.currentTimeMillis() + ".png"));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                log.debug("Try to parse driverPage");
                Document doc = Jsoup.parse(driver.getPageSource());

                String name1 = doc.select("div.tname__text>a").first().text();
                String name2 = doc.select("div.tname__text>a").last().text();
                String kickoff = "";
                Elements elements = doc.select("td.phrase");
                for (Element element : elements) {
                    if (element.text().contains("kick-off") || element.text().contains("will kick the game off")) {
                        kickoff = element.text();
                    }
                }
                if (kickoff.contains(name1)) kickoff = name1;
                if (kickoff.contains(name2)) kickoff = name2;

                log.info(name1 + ";" + name2 + ";" + kickoff);
                writer.write(name1 + ";" + name2 + ";" + kickoff + ";\r\n");
                writer.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void collectAllMatchesLiveResult(String url, String fileName) {
        List<String> ids = getAllLinksLiveResult(url);
        final String template = "https://www.live-result.com";
        try (FileWriter writer = new FileWriter(new File("C:\\screens\\" + fileName), true)) {
            for (String id : ids) {
                log.debug("Try to get " + template + id);
                try {
                    driver.get(template + id);
                } catch (org.openqa.selenium.TimeoutException e) {
                    log.error(e.getMessage());
                    continue;
                }

                log.debug("Try to parse driverPage");
                Document doc = Jsoup.parse(driver.getPageSource());

                String name1 = doc.select("div.team1>a>div.name").text();
                String name2 = doc.select("div.team2>a>div.name").text();
                String kickoff = "";
                Elements elements = doc.select("div.m");
                for (Element element : elements) {
                    if (element.text().contains("kick-off")) {
                        kickoff = element.text();
                    }
                }
                if (kickoff.contains(name1)) kickoff = name1;
                if (kickoff.contains(name2)) kickoff = name2;

                log.info(name1 + ";" + name2 + ";" + kickoff);
                writer.write(name1 + ";" + name2 + ";" + kickoff + ";\r\n");
                writer.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public Map analyze(String fileName) {
        Map<String, CommandStat> stat = new TreeMap<>();
        try {
            Scanner scanner = new Scanner(new File(fileName)).useDelimiter(";");


            while (scanner.hasNextLine()) {
                Scanner line = new Scanner(scanner.nextLine()).useDelimiter(";");
                String name1 = String.valueOf(line.next());
                String name2 = String.valueOf(line.next());
                String start = String.valueOf(line.next());
                if (!stat.containsKey(name1)) {
                    stat.put(name1, new CommandStat(0, 0, 0, 0));
                }
                if (!stat.containsKey(name2)) {
                    stat.put(name2, new CommandStat(0, 0, 0, 0));
                }

                if (name1.equals(start)) {
                    CommandStat commandStat = stat.get(name1);
                    commandStat.setMatchesAtHome(commandStat.getMatchesAtHome() + 1);
                    commandStat.setStartAtHome(commandStat.getStartAtHome() + 1);

                    CommandStat commandStatAway = stat.get(name2);
                    commandStatAway.setMatchesAway(commandStatAway.getMatchesAway() + 1);
                }
                if (name2.equals(start)) {
                    CommandStat commandStat = stat.get(name2);
                    commandStat.setMatchesAway(commandStat.getMatchesAway() + 1);
                    commandStat.setStartAway(commandStat.getStartAway() + 1);

                    CommandStat commandStatHome = stat.get(name1);
                    commandStatHome.setMatchesAtHome(commandStatHome.getMatchesAtHome() + 1);
                }

            }
            log.info(fileName);
            stat.entrySet().forEach(System.out::print);
            //log.info(Arrays.toString(stat.entrySet().toArray()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return stat;
    }

    public String headToHead(String fileName, String command1, String command2) {
        StringBuilder resultLine = new StringBuilder();
        try {
            Scanner scanner = new Scanner(new File(fileName)).useDelimiter(";");

            while (scanner.hasNextLine()) {
                Scanner line = new Scanner(scanner.nextLine()).useDelimiter(";");
                String name1 = String.valueOf(line.next());
                String name2 = String.valueOf(line.next());
                String start = String.valueOf(line.next());

                if ((name1.equals(command1) && name2.equals(command2)) || (name2.equals(command1) && name1.equals(command2)))
                    resultLine.append(name1).append("-").append(name2).append(":").append(start);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return resultLine.toString();
    }

    public void getStatToXls(Map statPrev, Map statCurrent, String fileCurrent, Sheet sheet, String command1, String command2, int rowNumber, String startTime) {
        CommandStat command1StatPrevious = (CommandStat) statPrev.get(command1);
        CommandStat command2StatPrevious = (CommandStat) statPrev.get(command2);
        CommandStat command1StatCurrent = (CommandStat) statCurrent.get(command1);
        CommandStat command2StatCurrent = (CommandStat) statCurrent.get(command2);

        String headToHead = headToHead(fileCurrent, command1, command2);


        Row row = sheet.createRow(rowNumber);

        row.createCell(0).setCellValue(startTime+" " + command1 + "-" + command2);
        row.createCell(1).setCellValue(command1StatPrevious.getStartAtHome() + "/" + command1StatPrevious.getMatchesAtHome() +
                " - " + command2StatPrevious.getStartAway() + "/" + command2StatPrevious.getMatchesAway());
        row.createCell(2).setCellValue(command1StatCurrent.getStartAtHome() + "/" + command1StatCurrent.getMatchesAtHome() +
                " - " + command2StatCurrent.getStartAway() + "/" + command2StatCurrent.getMatchesAway());
        row.createCell(3).setCellValue(headToHead);

    }

    public void getXls(String filePrevious, String fileCurrent, List<Match> matchList) {
        Map statPrev = analyze(filePrevious);
        Map statCurr = analyze(fileCurrent);

        Workbook wb = new HSSFWorkbook();
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet("new sheet");
        int rowNumber = 0;
        for (Match match : matchList) {
            getStatToXls(statPrev, statCurr, fileCurrent, sheet, match.getHomeCommand().getName(), match.getAwayCommand().getName(), rowNumber, match.getStartTime());
            rowNumber++;
        }
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
        try (OutputStream fileOut = new FileOutputStream("workbook.xls")) {
            wb.write(fileOut);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Scheduled(cron = "0 15 10 ? * TUE")
    public void getAllXls(){
        List<Match> matches = lineMatchService.getMatchesFlashScore(env.getProperty("england.url"));
        getXls(env.getProperty("england.prev"), env.getProperty("england.curr"), matches);

        mailService.sendWithFile("England", "", env.getProperty("email.adress.1"), "workbook.xls");
        mailService.sendWithFile("England","", env.getProperty("email.adress.2"), "workbook.xls");

        matches = lineMatchService.getMatchesFlashScore(env.getProperty("italy.url"));
        getXls(env.getProperty("italy.prev"), env.getProperty("italy.curr"), matches);
        mailService.sendWithFile("Italy", "", env.getProperty("email.adress.1"), "workbook.xls");
        mailService.sendWithFile("Italy","", env.getProperty("email.adress.2"), "workbook.xls");

        matches = lineMatchService.getMatchesFlashScore(env.getProperty("spain.url"));
        getXls(env.getProperty("spain.prev"), env.getProperty("spain.curr"), matches);
        mailService.sendWithFile("Spain", "", env.getProperty("email.adress.1"), "workbook.xls");
        mailService.sendWithFile("Spain","", env.getProperty("email.adress.2"), "workbook.xls");

        matches = lineMatchService.getMatchesFlashScore(env.getProperty("germany.url"));
        getXls(env.getProperty("germany.prev"), env.getProperty("germany.curr"), matches);
        mailService.sendWithFile("Germany", "", env.getProperty("email.adress.1"), "workbook.xls");
        mailService.sendWithFile("Germany","", env.getProperty("email.adress.2"), "workbook.xls");
    }



}

