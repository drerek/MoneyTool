package com.privatecompany.app.MoneyTool.service;

import com.privatecompany.app.MoneyTool.entity.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
        log.debug("Compare line and line football");

        List<Match> flashScoreMatches = lineMatchService.getMatchesFlashScore(env.getProperty("flashscore.url"));
        List<Match> oneXBetMatches = lineMatchService.getMatchesPariMatch("", "Football");

        List<Match> nonConfirmingMatches = compareMatches(oneXBetMatches, flashScoreMatches);

        StringBuilder mail = new StringBuilder("<b> Parimatch </b><br>Total parimatch matches:" + oneXBetMatches.size() + "<br>" +
                "Total flashscore matches:" + flashScoreMatches.size() + "<table border=\"1\"><tr><td>parimatch</td><td>flashscore</td></tr>");
        if (!nonConfirmingMatches.isEmpty()) {
            for (int i = 0; i < nonConfirmingMatches.size(); i = i + 2) {
                mail.append("<tr><td>").append(nonConfirmingMatches.get(i)).append("</td>").append("<td>").append(nonConfirmingMatches.get(i + 1)).append("</td></tr>");
            }
        }
        mail.append("</table>");
        log.debug("Try to send e-message");
        mailService.send("Line vs line football", mail.toString(), env.getProperty("email.adress.1"));
        mailService.send("Line vs line football", mail.toString(), env.getProperty("email.adress.2"));
		mailService.send("Line vs line football", mail.toString(), env.getProperty("email.adress.3"));
    }

//    @Scheduled(cron = "0 15,25,35,45 * * * ?")
    public void nonConfirmingTimeLineAndLiveMatches() {
        log.debug("Compare live and line");
        List<Match> flashscoreLiveMatches = liveMatchService.getMatches(env.getProperty("flashscore.url"));
        List<Match> oneXBetLineMatches = lineMatchService.getMatches1xbetv2(env.getProperty("1xbetexchane.url"));

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
			mailService.send("Live vs line", mail.toString(), env.getProperty("email.adress.3"));
        }
    }
// Hockey
    @Scheduled(cron = "0 3 1-23/2 * * ?")
    public void nonConfirmingTimeLineMatchHockey() {
        log.debug("Compare line and line hockey");

        List<Match> flashScoreMatches = lineMatchService.getMatchesFlashScore(env.getProperty("flashscorehockey.url"));
        List<Match> oneXBetMatches = lineMatchService.getMatchesPariMatch("", "Hockey");

        List<Match> nonConfirmingMatches = compareMatches(oneXBetMatches, flashScoreMatches);

        StringBuilder mail = new StringBuilder("Total parimatch matches:" + oneXBetMatches.size() + "<br>" +
                "Total flashscore matches:" + flashScoreMatches.size() + "<table border=\"1\"><tr><td>parimatch</td><td>flashscore</td></tr>");
        if (!nonConfirmingMatches.isEmpty()) {
            for (int i = 0; i < nonConfirmingMatches.size(); i = i + 2) {
                mail.append("<tr><td>").append(nonConfirmingMatches.get(i)).append("</td>").append("<td>").append(nonConfirmingMatches.get(i + 1)).append("</td></tr>");
            }
        }
        mail.append("</table>");
        log.debug("Try to send e-message");
        mailService.send("Line vs line hockey", mail.toString(), env.getProperty("email.adress.1"));
        mailService.send("Line vs line hockey", mail.toString(), env.getProperty("email.adress.2"));
		mailService.send("Line vs line hockey", mail.toString(), env.getProperty("email.adress.3"));
    }

//    @Scheduled(cron = "0 20,40 * * * ?")
    public void nonConfirmingTimeLineAndLiveMatchesHockey() {
        log.debug("Compare live and line");
        List<Match> flashscoreLiveMatches = liveMatchService.getMatches(env.getProperty("flashscorehockey.url"));
        List<Match> oneXBetLineMatches = lineMatchService.getMatchesPariMatch("", "Hockey");

        List<Match> nonConfirmingMatches = compareMatches(oneXBetLineMatches, flashscoreLiveMatches);
        if (!nonConfirmingMatches.isEmpty()) {
            StringBuilder mail = new StringBuilder("Total 1xbet line matches:" + oneXBetLineMatches.size() + "<br>" +
                    "Total flashscore live matches:" + flashscoreLiveMatches.size() + "<br>"+"<table border=\"1\"><tr><td>1xbet line</td><td>flashscore live</td></tr>");
            for (int i = 0; i < nonConfirmingMatches.size(); i = i + 2) {
                mail.append("<tr><td>").append(nonConfirmingMatches.get(i)).append("</td>").append("<td>").append(nonConfirmingMatches.get(i + 1)).append("</td></tr>");
            }
            mail.append("</table>");

            log.debug("Try to send e-message");
            mailService.send("Live vs line hockey", mail.toString(), env.getProperty("email.adress.1"));
            mailService.send("Live vs line hockey", mail.toString(), env.getProperty("email.adress.2"));
			mailService.send("Live vs line hockey", mail.toString(), env.getProperty("email.adress.3"));
        }
    }

    @Scheduled(cron = "0 30 10 * * ?")
    public void sendMorning(){
        log.debug("Try to send morning");
        List<String> morning = KsushaMessages.getMorning();

        List<String> specialWords = KsushaMessages.getSpecialWord();

        int number = (int)(Math.random()*morning.size())+1;
        String message = morning.get(number);

        number = (int)(Math.random()*specialWords.size())+1;
        String word = specialWords.get(number);

        String[] messageWithDil = message.split("\n");

        StringBuilder messageTextBuilder = new StringBuilder("Доброе утро, <b>");
        messageTextBuilder.append(word);
        messageTextBuilder.append("</b><br> Следующие слова - для тебя! <br><br>");
        for (String mes: messageWithDil){
            messageTextBuilder.append(mes).append("<br>");
        }

        messageTextBuilder.append("<br> Пиши мне, после каждой рассылки :*" +
                "<br> P.S. У нас все будет хорошо, вот посмотришь!" +
                "<br> P.S.S. Целую и обнимаю! :*");

        String messageText = messageTextBuilder.toString();

        log.debug("Try to send e-message");

        number = (int)(Math.random()*specialWords.size())+1;
        word = specialWords.get(number);
        mailService.send("Доброе утро, "+ word + "!", messageText, env.getProperty("email.adress.1"));
        mailService.send("Доброе утро, "+ word + "!", messageText, env.getProperty("email.adress.3"));
    }


    @Scheduled(cron = "0 30 14 * * ?")
    public void sendDay(){
        log.debug("Try to send day");
        List<String> day = KsushaMessages.getDay();

        List<String> specialWords = KsushaMessages.getSpecialWord();

        int number = (int)(Math.random()*day.size())+1;
        String message = day.get(number);

        number = (int)(Math.random()*specialWords.size())+1;
        String word = specialWords.get(number);

        String[] messageWithDil = message.split("\n");

        StringBuilder messageTextBuilder = new StringBuilder("Доброе день, <b>");
        messageTextBuilder.append(word);
        messageTextBuilder.append("</b> <br> Следующие слова - для тебя! <br><br>");
        for (String mes: messageWithDil){
            messageTextBuilder.append(mes).append("<br>");
        }

        messageTextBuilder.append("<br> Пиши мне, после каждой рассылки :*" +
                "<br> P.S. У нас все будет хорошо, вот посмотришь!" +
                "<br> P.S.S. Целую и обнимаю! :*");

        String messageText = messageTextBuilder.toString();

        log.debug("Try to send e-message");

        number = (int)(Math.random()*specialWords.size())+1;
        word = specialWords.get(number);
        mailService.send("Добрый день, " + word + "!", messageText, env.getProperty("email.adress.1"));
        mailService.send("Доброе день, " + word + "!", messageText, env.getProperty("email.adress.3"));
    }


    @Scheduled(cron = "0 30 23 * * ?")
    public void sendNight(){
        log.debug("Try to send night");
        List<String> night = KsushaMessages.getNight();
        List<String> specialWords = KsushaMessages.getSpecialWord();

        int number = (int)(Math.random()*night.size())+1;
        String message = night.get(number);

        number = (int)(Math.random()*specialWords.size())+1;
        String word = specialWords.get(number);

        String[] messageWithDil = message.split("\n");

        StringBuilder messageTextBuilder = new StringBuilder("Доброй ночи, <b>");
        messageTextBuilder.append(word);
        messageTextBuilder.append("</b> <br> Следующие слова - для тебя! <br><br>");
        for (String mes: messageWithDil){
            messageTextBuilder.append(mes).append("<br>");
        }

        messageTextBuilder.append("<br> Пиши мне, после каждой рассылки :*" +
                "<br> P.S. У нас все будет хорошо, вот посмотришь!" +
                "<br> P.S.S. Целую и обнимаю! :*");

        String messageText = messageTextBuilder.toString();

        log.debug("Try to send e-message");
        number = (int)(Math.random()*specialWords.size())+1;
        word = specialWords.get(number);
        mailService.send("Добрый вечер, " + word + "!", messageText, env.getProperty("email.adress.1"));
        mailService.send("Добрый вечер, " + word + "!", messageText, env.getProperty("email.adress.3"));
    }
}
