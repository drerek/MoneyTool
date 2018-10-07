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

     @GetMapping("/analyzeLineMatches")
    public ResponseEntity<List<Match>> analyzeMatches() {
        analyzeService.nonConfirmingTimeLineMatch();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
