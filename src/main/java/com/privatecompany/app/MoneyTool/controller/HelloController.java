package com.privatecompany.app.MoneyTool.controller;

import com.privatecompany.app.MoneyTool.entity.Command;
import com.privatecompany.app.MoneyTool.entity.Match;
import com.privatecompany.app.MoneyTool.service.AnalyzeService;
import com.privatecompany.app.MoneyTool.service.LineMatchService;
import com.privatecompany.app.MoneyTool.service.LiveMatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HelloController {

    private final LineMatchService lineMatchService;

    private final LiveMatchService liveMatchService;

    private final AnalyzeService analyzeService;

    @Autowired
    public HelloController(LineMatchService lineMatchService, LiveMatchService liveMatchService, AnalyzeService analyzeService) {
        this.lineMatchService = lineMatchService;
        this.liveMatchService = liveMatchService;
        this.analyzeService = analyzeService;
    }

    @RequestMapping("/")
    public String index() {
        return "Greetings from Spring Boot!";
    }

    @RequestMapping("/linecommands")
    public ResponseEntity<List<Command>> getLineMatches(){
            return new ResponseEntity<>(lineMatchService.getCommands(),HttpStatus.OK);
    }

    @GetMapping("/livecommands")
    public ResponseEntity<List<Command>> getLiveMatches(){
        return new ResponseEntity<>(liveMatchService.getCommands(),HttpStatus.OK);
    }

    @GetMapping("/flashscorelinematches")
    public ResponseEntity<List<Match>> getLineMatchesFlashscore() {
        return new ResponseEntity<>(lineMatchService.getMatchesFlashScore(),HttpStatus.OK);
    }

    @GetMapping("/1xlinematches")
    public ResponseEntity<List<Match>> getLineMatches1x() {
        return new ResponseEntity<>(lineMatchService.getMatches1xbet(),HttpStatus.OK);
    }

    @GetMapping("/analyzeLineMatches")
    public ResponseEntity<List<Match>> analyzeMatches() {
        return new ResponseEntity<>(analyzeService.nonConfirmingTimeLineMatch(),HttpStatus.OK);
    }
}
