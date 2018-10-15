package com.privatecompany.app.MoneyTool.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerDistance;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

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

        if (thisMatcherHome.find() && thisMatcherAway.find() && secondMatcherHome.find() && secondMatcherAway.find()) {
            thisHomeCommand = thisHomeCommand.substring(0, thisMatcherHome.start());
            thisAwayCommand = thisAwayCommand.substring(0, thisMatcherAway.start());
            secondHomeCommand = secondHomeCommand.substring(0, secondMatcherHome.start());
            secondAwayCommand = secondAwayCommand.substring(0, secondMatcherAway.start());
        }

        if (distance.apply(thisHomeCommand, secondHomeCommand) > 0.8
                && distance.apply(thisAwayCommand, secondAwayCommand) > 0.6)
            return true;
        if (distance.apply(thisHomeCommand, secondAwayCommand) > 0.8
                && distance.apply(thisAwayCommand, secondHomeCommand) > 0.6)
            return true;
        return false;
    }

    public boolean compareMatchTime(Match secondMatch) {
        LocalDate firstMatchTime = LocalDate.parse(this.getStartTime(), formatter);
        LocalDate secondMatchTime = LocalDate.parse(secondMatch.getStartTime(), formatter);
        return firstMatchTime.isAfter(secondMatchTime);
    }
}
