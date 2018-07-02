package com.privatecompany.app.MoneyTool.controller;

import com.privatecompany.app.MoneyTool.entity.Command;
import com.privatecompany.app.MoneyTool.service.LineMatchService;
import com.privatecompany.app.MoneyTool.service.LiveMatchService;
import org.apache.http.protocol.HTTP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class HelloController {

    @Autowired
    private LineMatchService lineMatchService;

    @Autowired
    private LiveMatchService liveMatchService;

    @GetMapping("linematches/")
    public ResponseEntity<List<Command>> getLineMatches(){
            return new ResponseEntity<>(lineMatchService.getCommands(),HttpStatus.OK);
    }

    @GetMapping("livematches/")
    public ResponseEntity<List<Command>> getLiveMatches(){
        return new ResponseEntity<>(liveMatchService.getCommands(),HttpStatus.OK);
    }
}
