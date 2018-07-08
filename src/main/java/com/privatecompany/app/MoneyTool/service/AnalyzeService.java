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

    @Scheduled(cron = "0 0 */2 * * ?")
    public List<Match> nonConfirmingTimeLineMatch() {
        log.debug("Compare line and line");
        List<Match> flashscoreMatches = lineMatchService.getMatchesFlashScore();
        List<Match> oneXBetMatches = lineMatchService.getMatches1xbet();
        List<Match> nonConfirmingMathces = new LinkedList<>();
        List<Match> comparedMatches = new LinkedList<>();
        Set<Match> nonComparedMatches = new HashSet<>();
        List<Match> comparableFlashScoreMatch = new LinkedList<>();

        int total1xLine= oneXBetMatches.size();
        log.debug("Total 1x matches:" + oneXBetMatches.size());
        int totalFlashScoreLineMatches = flashscoreMatches.size();
        log.debug("Total flashscore matches" + flashscoreMatches.size());
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
                    //log.debug(oneXBetMatch + " was compared");
                    comparedMatches.add(oneXBetMatch);
                    if (!oneXBetMatch.getStartTime().equals(flashscoreMatch.getStartTime())) {
                        nonConfirmingMathces.add(oneXBetMatch);
                        comparableFlashScoreMatch.add(flashscoreMatch);
                        log.error(oneXBetMatch + "and" + flashscoreMatch + "TIME NOT EQUALS");
                    }
                }
            }
        }
        oneXBetMatches.removeAll(comparedMatches);
        log.debug("Try to send e-message");
        mailService.send("Money", "Total 1xbet matches:"+ total1xLine +"\n" +
                "Total flashscore matches" + totalFlashScoreLineMatches + "\n" +
                "1xbet match:"+String.valueOf(nonConfirmingMathces)+
                " flashscore match:"+ String.valueOf(comparableFlashScoreMatch)+"\n", "adrerek@gmail.com");
        mailService.send("Money", "Total 1xbet matches:"+ total1xLine +"\n" +
                "Total flashscore matches" + totalFlashScoreLineMatches + "\n" +
                "1xbet match:"+String.valueOf(nonConfirmingMathces)+
                " flashscore match:"+ String.valueOf(comparableFlashScoreMatch)+"\n", "artichsa@yandex.ua");
        return oneXBetMatches;
    }

    @Scheduled(cron = "0 5,20,35,50 * * * ?")
    public List<Match> nonConfirmingTimeLineAndLiveMatches() {
        log.debug("Compare live and line");
        List<Match> flashscoreLiveMatches = liveMatchService.getMatches();
        List<Match> oneXBetLineMatches = lineMatchService.getMatches1xbet();

        log.debug("Total 1x line matches:" + oneXBetLineMatches.size());


        log.debug("Total flashscore live matches" + flashscoreLiveMatches.size());
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
        if (!comparedMatches.isEmpty()) {
            log.debug("Try to send e-message");
            mailService.send("Live vs line money", "Attention: matches" + String.valueOf(comparedMatches), "adrerek@gmail.com");
            mailService.send("Live vs line money", "Attention: matches" + String.valueOf(comparedMatches), "artichsa@yandex.ua");
        }
        return comparedMatches;
    }
}
