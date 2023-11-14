package com.evgeny.bsuirapp.models.json_models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.*;

@Getter
@Setter
@ToString
public class Schedule {
    private List<Day> Четверг;
    private List<Day> Пятница;
    private List<Day> Вторник;
    private List<Day> Понедельник;
    private List<Day> Среда;
    private List<Day> Суббота;

//    public List<Day> getAllDays(){
//        List<Day> resultList = new ArrayList<>();
//        resultList.addAll(Понедельник);
//        resultList.addAll(Вторник);
//        resultList.addAll(Среда);
//        resultList.addAll(Четверг);
//        resultList.addAll(Пятница);
//        resultList.addAll(Суббота);
//        return resultList;
//    }
    public Map<String, List<Day>> getAllDays(){
        Map<String, List<Day>> listToDayReference = new LinkedHashMap<>();
        listToDayReference.put("Понедельник", Понедельник);
        listToDayReference.put("Вторник", Вторник);
        listToDayReference.put("Среда", Среда);
        listToDayReference.put("Четверг", Четверг);
        listToDayReference.put("Пятница", Пятница);
        listToDayReference.put("Суббота", Суббота);
        return listToDayReference;
    }
}
