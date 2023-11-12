package com.evgeny.bsuirapp.models.json_models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Schedule {
    private List<Day> Четверг;
    private List<Day> Пятница;
    private List<Day> Вторник;
    private List<Day> Понедельник;
    private List<Day> Среда;
}
