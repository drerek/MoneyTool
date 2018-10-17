package com.privatecompany.app.MoneyTool.controller;

import com.privatecompany.app.MoneyTool.entity.Match;
import com.privatecompany.app.MoneyTool.service.AnalyzeService;
import com.privatecompany.app.MoneyTool.service.LineMatchService;
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

    private final AnalyzeService analyzeService;

    @Autowired
    public HelloController(LineMatchService lineMatchService, AnalyzeService analyzeService) {
        this.lineMatchService = lineMatchService;
        this.analyzeService = analyzeService;
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
}
