package com.privatecompany.app.MoneyTool.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Match {
    private Command homeCommand;
    private Command awayCommand;
    private String startTime;

    @Override
    public String toString() {
        return "Match:" +
                homeCommand +
                "-" + awayCommand +
                " at " + startTime;
    }
}
