package com.privatecompany.app.MoneyTool.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommandStat {
    Integer startAtHome;
    Integer matchesAtHome;
    Integer startAway;
    Integer matchesAway;

    @Override
    public String toString() {
        return  "startAtHome=" + startAtHome +
                ", matchesAtHome=" + matchesAtHome +
                ", startAway=" + startAway +
                ", matchesAway=" + matchesAway+"\n";
    }
}
