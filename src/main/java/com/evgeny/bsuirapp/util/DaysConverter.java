package com.evgeny.bsuirapp.util;

import com.evgeny.bsuirapp.models.json_models.Day;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Component
public class DaysConverter {
    private static final Map<DayOfWeek, String> daysOfWeek;

    static {
        daysOfWeek = new HashMap<>();
        daysOfWeek.put(DayOfWeek.MONDAY, "Понедельник");
        daysOfWeek.put(DayOfWeek.TUESDAY, "Вторник");
        daysOfWeek.put(DayOfWeek.WEDNESDAY, "Среда");
        daysOfWeek.put(DayOfWeek.THURSDAY, "Четверг");
        daysOfWeek.put(DayOfWeek.FRIDAY, "Пятница");
        daysOfWeek.put(DayOfWeek.SATURDAY, "Суббота");
        daysOfWeek.put(DayOfWeek.SUNDAY, "Воскресенье");
    }

    public static String getTodayOrTomorrow(String optionOfDay) {
        LocalDate date = LocalDate.now();
        DayOfWeek currDayOfWeek = date.getDayOfWeek();
        if (currDayOfWeek == DayOfWeek.SUNDAY) {
            return "Ошибка";
        }
        switch (optionOfDay) {
            case "/today" -> {
                return daysOfWeek.get(currDayOfWeek);
            }
            case "/tomorrow" -> {
                currDayOfWeek = currDayOfWeek.plus(1);
                if (currDayOfWeek == DayOfWeek.SUNDAY) {
                    return "Ошибка";
                }
                return daysOfWeek.get(currDayOfWeek);
            }
            default -> {
                return "Ошибка";
            }
        }
    }
}
