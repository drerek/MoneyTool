package com.privatecompany.app.MoneyTool.controller;

import com.privatecompany.app.MoneyTool.entity.Match;
import com.privatecompany.app.MoneyTool.service.AnalyzeService;
import com.privatecompany.app.MoneyTool.service.LineMatchService;
import com.privatecompany.app.MoneyTool.service.StatisticService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HelloController {

    private final LineMatchService lineMatchService;

    private final AnalyzeService analyzeService;

    private final StatisticService statisticService;

    @Autowired
    public HelloController(LineMatchService lineMatchService, AnalyzeService analyzeService, StatisticService statisticService) {
        this.lineMatchService = lineMatchService;
        this.analyzeService = analyzeService;
        this.statisticService = statisticService;
    }

     @GetMapping("/football")
    public ResponseEntity<List<Match>> analyzeMatches() {
        analyzeService.nonConfirmingTimeLineMatch();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/hockey")
    public ResponseEntity<List<Match>> analyzeMatchesHockey() {
        analyzeService.nonConfirmingTimeLineMatchHockey();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/stat")
    public ResponseEntity stat(){

//        statisticService.analyze("russia.txt");
//        statisticService.analyze("bundes20172018.txt");
//        statisticService.analyze("laliga.txt");
//        statisticService.analyze("france.txt");
       statisticService.collectAllMatches("https://www.flashscore.com/football/europe/uefa-nations-league/results/", "leagueNations.txt");

        statisticService.analyze("leagueNations.txt");
       // statisticService.analyze("apl20162018.txt");
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/statlivereult")
    public ResponseEntity statLiveResult(){
        statisticService.collectAllMatchesLiveResult("https://www.liveresult.ru/football/Russia/Premier-League/2017-2018/results/", "russia.txt");
        statisticService.analyze("russia.txt");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
