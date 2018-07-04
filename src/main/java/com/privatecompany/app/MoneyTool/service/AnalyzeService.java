package com.privatecompany.app.MoneyTool.service;

import com.privatecompany.app.MoneyTool.entity.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
@PropertySource("classpath:links.properties")
public class AnalyzeService {
    private static Logger log = LoggerFactory.getLogger(AnalyzeService.class);

    private final Environment env;
    private final LineMatchService lineMatchService;
    private final MailService mailService;
    private final LiveMatchService liveMatchService;

    @Autowired
    public AnalyzeService(Environment env, LineMatchService lineMatchService, MailService mailService, LiveMatchService liveMatchService) {
        this.env = env;
        this.lineMatchService = lineMatchService;
        this.mailService = mailService;
        this.liveMatchService = liveMatchService;
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public List<Match> nonConfirmingTimeLineMatch() {
        log.debug("Compare line and line");
        List<Match> flashscoreMatches = lineMatchService.getMatchesFlashScore();
        List<Match> oneXBetMatches = lineMatchService.getMatches1xbet();
        List<Match> nonConfirmingMathces = new LinkedList<>();
        List<Match> comparedMatches = new LinkedList<>();
        Set<Match> nonComparedMatches = new HashSet<>();
        log.debug("Total 1x matches:"+oneXBetMatches.size());
        log.debug("Total flashscore matches"+flashscoreMatches.size());
        for (Match oneXBetMatch : oneXBetMatches) {
            for (Match flashscoreMatch : flashscoreMatches) {
                if (flashscoreMatch.getHomeCommand().getName().contains(oneXBetMatch.getHomeCommand().getName()) ||
                        flashscoreMatch.getHomeCommand().getName().contains(oneXBetMatch.getAwayCommand().getName()) ||
                        flashscoreMatch.getAwayCommand().getName().contains(oneXBetMatch.getHomeCommand().getName()) ||
                        flashscoreMatch.getAwayCommand().getName().contains(oneXBetMatch.getAwayCommand().getName()) ||

                        oneXBetMatch.getHomeCommand().getName().contains(flashscoreMatch.getHomeCommand().getName()) ||
                        oneXBetMatch.getHomeCommand().getName().contains(flashscoreMatch.getAwayCommand().getName()) ||
                        oneXBetMatch.getAwayCommand().getName().contains(flashscoreMatch.getHomeCommand().getName()) ||
                        oneXBetMatch.getAwayCommand().getName().contains(flashscoreMatch.getAwayCommand().getName())) {
                    log.debug(oneXBetMatch + " was compared");
                    comparedMatches.add(oneXBetMatch);
                    if (!oneXBetMatch.getStartTime().equals(flashscoreMatch.getStartTime())){
                        nonConfirmingMathces.add(oneXBetMatch);
                        log.error(oneXBetMatch + "TIME NOT EQUALS");
                    }
                }
            }
        }
        oneXBetMatches.removeAll(comparedMatches);

        log.debug("Try to send e-message");
        mailService.send("Money", String.valueOf(nonConfirmingMathces), "adrerek@gmail.com");
        return oneXBetMatches;
    }

    @Scheduled(cron = "*/15 * * * *")
    public List<Match> nonConfirmingTimeLineAndLiveMatches(){
        log.debug("Compare live and line");
        List<Match> flashscoreLiveMatches = liveMatchService.getMatches();
        List<Match> oneXBetLineMatches = lineMatchService.getMatches1xbet();
        log.debug("Total 1x matches:"+oneXBetLineMatches.size());
        log.debug("Total flashscore matches"+flashscoreLiveMatches.size());
        List<Match> comparedMatches = new LinkedList<>();

        for (Match oneXBetMatch : oneXBetLineMatches) {
            for (Match flashscoreMatch : flashscoreLiveMatches) {
                if (flashscoreMatch.getHomeCommand().getName().contains(oneXBetMatch.getHomeCommand().getName()) ||
                        flashscoreMatch.getHomeCommand().getName().contains(oneXBetMatch.getAwayCommand().getName()) ||
                        flashscoreMatch.getAwayCommand().getName().contains(oneXBetMatch.getHomeCommand().getName()) ||
                        flashscoreMatch.getAwayCommand().getName().contains(oneXBetMatch.getAwayCommand().getName()) ||

                        oneXBetMatch.getHomeCommand().getName().contains(flashscoreMatch.getHomeCommand().getName()) ||
                        oneXBetMatch.getHomeCommand().getName().contains(flashscoreMatch.getAwayCommand().getName()) ||
                        oneXBetMatch.getAwayCommand().getName().contains(flashscoreMatch.getHomeCommand().getName()) ||
                        oneXBetMatch.getAwayCommand().getName().contains(flashscoreMatch.getAwayCommand().getName())) {
                    log.debug(oneXBetMatch + " was compared");
                    comparedMatches.add(oneXBetMatch);
                }
            }
        }
        log.debug("Try to send e-message");
        mailService.send("Live vs line money", "All works: matches" + String.valueOf(comparedMatches), "adrerek@gmail.com");
        return comparedMatches;
    }
}
