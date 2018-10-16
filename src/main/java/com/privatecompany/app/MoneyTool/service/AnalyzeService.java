package com.privatecompany.app.MoneyTool.service;

import com.privatecompany.app.MoneyTool.entity.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

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
                if (oneXBetMatch.compareMatchName(flashScoreMatch) && oneXBetMatch.compareMatchTime(flashScoreMatch)) {
                    notConfirmingMatches.add(oneXBetMatch);
                    notConfirmingMatches.add(flashScoreMatch);
                }
            }
        }
        return notConfirmingMatches;
    }


    @Scheduled(cron = "0 3 */2 * * ?")
    public void nonConfirmingTimeLineMatch() {
        log.debug("Compare line and line");

        List<Match> flashScoreMatches = lineMatchService.getMatchesFlashScore();
        List<Match> oneXBetMatches = lineMatchService.getMatches1xbetv2();

        List<Match> nonConfirmingMatches = compareMatches(oneXBetMatches, flashScoreMatches);

        StringBuilder mail = new StringBuilder("Total 1xbet matches:" + oneXBetMatches.size() + "<br>" +
                "Total flashscore matches:" + flashScoreMatches.size() + "br"+ "<table border=\"1\"><tr><td>1xbet</td><td>flashscore</td></tr>");
        if (!nonConfirmingMatches.isEmpty()) {
            for (int i = 0; i < nonConfirmingMatches.size(); i = i + 2) {
                mail.append("<tr><td>").append(nonConfirmingMatches.get(i)).append("</td>").append("<td>").append(nonConfirmingMatches.get(i + 1)).append("</td></tr>");
            }
        }
        mail.append("</table>");
        log.debug("Try to send e-message");
        mailService.send("Line vs line", mail.toString(), env.getProperty("email.adress.1"));
        mailService.send("Line vs line", mail.toString(), env.getProperty("email.adress.2"));
    }

    @Scheduled(cron = "0 20,35,50 * * * ?")
    public void nonConfirmingTimeLineAndLiveMatches() {
        log.debug("Compare live and line");
        List<Match> flashscoreLiveMatches = liveMatchService.getMatches();
        List<Match> oneXBetLineMatches = lineMatchService.getMatches1xbetv2();

        List<Match> nonConfirmingMatches = compareMatches(oneXBetLineMatches, flashscoreLiveMatches);
        if (!nonConfirmingMatches.isEmpty()) {
            StringBuilder mail = new StringBuilder("Total 1xbet line matches:" + oneXBetLineMatches.size() + "<br>" +
                    "Total flashscore live matches:" + flashscoreLiveMatches.size() + "<br>"+"<table border=\"1\"><tr><td>1xbet line</td><td>flashscore live</td></tr>");
            for (int i = 0; i < nonConfirmingMatches.size(); i = i + 2) {
                mail.append("<tr><td>").append(nonConfirmingMatches.get(i)).append("</td>").append("<td>").append(nonConfirmingMatches.get(i + 1)).append("</td></tr>");
            }
            mail.append("</table>");

            log.debug("Try to send e-message");
            mailService.send("Live vs line", mail.toString(), env.getProperty("email.adress.1"));
            mailService.send("Live vs line", mail.toString(), env.getProperty("email.adress.2"));
        }
    }
}
