package com.privatecompany.app.MoneyTool.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.text.similarity.JaroWinklerDistance;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    private Command homeCommand;
    private Command awayCommand;
    private String startTime;
    private final JaroWinklerDistance distance = new JaroWinklerDistance();

    @Override
    public String toString() {
        return "Match:" +
                homeCommand +
                "-" + awayCommand +
                " at " + startTime;
    }

    public boolean compareMatchName(Match secondMatch){

        if (distance.apply(this.getHomeCommand().getName(),secondMatch.getHomeCommand().getName())>0.0
                && distance.apply(this.getAwayCommand().getName(),secondMatch.getAwayCommand().getName())>0.0)
            return true;
        if (distance.apply(this.getHomeCommand().getName(), secondMatch.getAwayCommand().getName())>0.0
                && distance.apply(this.getAwayCommand().getName(), secondMatch.getHomeCommand().getName())>0.0)
            return true;
        return false;
    }

    public boolean compareMatchTime(Match secondMatch){
        return this.getStartTime().equals(secondMatch.getStartTime());
    }
}
