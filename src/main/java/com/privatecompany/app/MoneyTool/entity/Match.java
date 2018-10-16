package com.privatecompany.app.MoneyTool.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerDistance;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    private Command homeCommand;
    private Command awayCommand;
    private String startTime;
    private final JaroWinklerDistance distance = new JaroWinklerDistance();
    private static final Pattern pattern = Pattern.compile("\\sU\\d{2}$");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public String toString() {
        return  homeCommand +
                "-" + awayCommand +
                " at " + startTime;
    }

    public boolean compareMatchName(Match secondMatch) {

        String thisHomeCommand = this.getHomeCommand().getName();
        String thisAwayCommand = this.getAwayCommand().getName();
        String secondHomeCommand = secondMatch.getHomeCommand().getName();
        String secondAwayCommand = secondMatch.getAwayCommand().getName();

        Matcher thisMatcherHome = pattern.matcher(thisHomeCommand);
        Matcher thisMatcherAway = pattern.matcher(thisAwayCommand);
        Matcher secondMatcherHome = pattern.matcher(secondHomeCommand);
        Matcher secondMatcherAway = pattern.matcher(secondAwayCommand);

        String thisLeague = "";
        String secondLeague = "";

        if (thisMatcherHome.find() && thisMatcherAway.find() && secondMatcherHome.find() && secondMatcherAway.find()) {
            thisHomeCommand = thisHomeCommand.substring(0, thisMatcherHome.start());
            thisAwayCommand = thisAwayCommand.substring(0, thisMatcherAway.start());
            thisLeague = this.getHomeCommand().getName().substring(thisMatcherHome.start()+1, this.getHomeCommand().getName().length());
            secondHomeCommand = secondHomeCommand.substring(0, secondMatcherHome.start());
            secondAwayCommand = secondAwayCommand.substring(0, secondMatcherAway.start());
            secondLeague = secondMatch.getHomeCommand().getName().substring(secondMatcherHome.start()+1, secondMatch.getHomeCommand().getName().length());
        }
        if (thisLeague.equals(secondLeague)) {
            if (distance.apply(thisHomeCommand, secondHomeCommand) > 0.8
                    && distance.apply(thisAwayCommand, secondAwayCommand) > 0.6)
                return true;
            if (distance.apply(thisHomeCommand, secondAwayCommand) > 0.8
                    && distance.apply(thisAwayCommand, secondHomeCommand) > 0.6)
                return true;
        }
        return false;
    }

    public boolean compareMatchTime(Match secondMatch) {
        LocalTime firstMatchTime = LocalTime.parse(this.getStartTime(), formatter);
        LocalTime secondMatchTime = LocalTime.parse(secondMatch.getStartTime(), formatter);
        return firstMatchTime.isAfter(secondMatchTime);
    }
}
