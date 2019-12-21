package com.privatecompany.app.MoneyTool.service;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class KsushaMessages {

    public static List<String> getMorning(){

        File file = new File("C://messages//morning.txt");
        try {
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            return Arrays.asList(content.split("----------"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getDay(){

        File file = new File("C://messages//day.txt");
        try {
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            return Arrays.asList(content.split("----------"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getNight(){

        File file = new File("C://messages//night.txt");
        try {
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            return Arrays.asList(content.split("----------"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> getSpecialWord(){
        File file = new File("C://messages//words.txt");
        try {
            String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            return Arrays.asList(content.split("----------"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
