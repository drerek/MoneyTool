package com.privatecompany.app.MoneyTool.service;

import com.privatecompany.app.MoneyTool.entity.Match;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.apache.commons.text.*;


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

    private List<Match> compareMatches(List<Match> oneXBetMatches, List<Match> flashScoreMatches) {
        List<Match> notConfirmingMatches = new LinkedList<>();
        for (Match oneXBetMatch : oneXBetMatches) {
            for (Match flashScoreMatch : flashScoreMatches) {
                if (oneXBetMatch.compareMatchName(flashScoreMatch)) {
                    if (!oneXBetMatch.compareMatchTime(flashScoreMatch)) {
                        notConfirmingMatches.add(oneXBetMatch);
                        notConfirmingMatches.add(flashScoreMatch);
                    }
                }
            }
        }
        return notConfirmingMatches;
    }


    @Scheduled(cron = "0 3 */2 * * ?")
    public void nonConfirmingTimeLineMatch() {
        log.debug("Compare line and line");
        List<Match> oneXBetMatches = lineMatchService.getMatches1xbet();
        List<Match> flashScoreMatches = lineMatchService.getMatchesFlashScore();

        List<Match> nonConfirmingMatches = compareMatches(oneXBetMatches, flashScoreMatches);

        StringBuilder mail = new StringBuilder("Total 1xbet matches:" + oneXBetMatches.size() + "\n" +
                "Total flashscore matches:" + flashScoreMatches.size() + "\n");
        if (!nonConfirmingMatches.isEmpty()) {
            for (int i = 0; i < nonConfirmingMatches.size() - 1; i = i + 2) {
                mail.append("1xbet:").append(nonConfirmingMatches.get(i)).append("\n").append("flashScore:").append(nonConfirmingMatches.get(i + 1)).append("\n");
            }
        }

        log.debug("Try to send e-message");
        mailService.send("Line vs line", mail.toString(), env.getProperty("email.adress.1"));
        mailService.send("Line vs line", mail.toString(), env.getProperty("email.adress.2"));
    }

    @Scheduled(cron = "0 5,20,35,50 * * * ?")
    public void nonConfirmingTimeLineAndLiveMatches() {
        log.debug("Compare live and line");
        List<Match> flashscoreLiveMatches = liveMatchService.getMatches();
        List<Match> oneXBetLineMatches = lineMatchService.getMatches1xbet();

        List<Match> nonConfirmingMatches = compareMatches(oneXBetLineMatches, flashscoreLiveMatches);
        if (!nonConfirmingMatches.isEmpty()) {
            StringBuilder mail = new StringBuilder("Total 1xbet line matches:" + oneXBetLineMatches.size() + "\n" +
                    "Total flashscore live matches:" + flashscoreLiveMatches.size() + "\n");
            for (int i = 0; i < nonConfirmingMatches.size() - 1; i = i + 2) {
                mail.append("1xbet line:").append(nonConfirmingMatches.get(i)).append("\n").append("flashScore live:").append(nonConfirmingMatches.get(i + 1)).append("\n");
            }


            log.debug("Try to send e-message");
            mailService.send("Live vs line", mail.toString(), env.getProperty("email.adress.1"));
            mailService.send("Live vs line", mail.toString(), env.getProperty("email.adress.2"));
        }
    }
}
