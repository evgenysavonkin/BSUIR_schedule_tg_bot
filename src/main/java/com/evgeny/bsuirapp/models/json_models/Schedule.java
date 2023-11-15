package com.evgeny.bsuirapp.models.json_models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String, List<Day>> getAllDays() {
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
