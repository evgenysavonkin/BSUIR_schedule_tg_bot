package com.evgeny.bsuirapp.service.impl;

import com.evgeny.bsuirapp.models.json_models.Day;
import com.evgeny.bsuirapp.models.json_models.ScheduleResponseDto;
import com.evgeny.bsuirapp.service.ApiService;
import com.evgeny.bsuirapp.service.JsonParser;
import com.evgeny.bsuirapp.service.ScheduleService;
import com.evgeny.bsuirapp.util.DaysConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ApiService apiService;
    private final JsonParser jsonParser;

    @Autowired
    public ScheduleServiceImpl(ApiService apiService, JsonParser jsonParser) {
        this.apiService = apiService;
        this.jsonParser = jsonParser;
    }

    @Override
    public String getFormattedScheduleByUserGroup(int groupId, String dayOption) {
        String rawJson = apiService.getRawScheduleJson(groupId);
        Object obj = jsonParser.parseJson(rawJson);
        ScheduleResponseDto scheduleResponseDto = null;
        if (obj instanceof ScheduleResponseDto) {
            scheduleResponseDto = (ScheduleResponseDto) obj;
        }
        if (scheduleResponseDto == null) {
            throw new RuntimeException();
        }
        int weekNumber = apiService.getNumberOfWeek();
        String dayOfWeekRussian = DaysConverter.getTodayOrTomorrow(dayOption);
        if (dayOfWeekRussian.equals("Ошибка"))
            return null;
        return parseScheduleResponseDto(dayOfWeekRussian, weekNumber, scheduleResponseDto);
    }

    private String parseScheduleResponseDto(String dayOfWeek, int weekNumber, ScheduleResponseDto responseDto) {
        List<Day> scheduleOfDay = null;
        switch (dayOfWeek) {
            case "Понедельник" -> {
                scheduleOfDay = responseDto.getSchedules().getПонедельник();
                return writeScheduleOfDay(scheduleOfDay, weekNumber);
            }
            case "Вторник" -> {
                scheduleOfDay = responseDto.getSchedules().getВторник();
                return writeScheduleOfDay(scheduleOfDay, weekNumber);
            }
            case "Среда" -> {
                scheduleOfDay = responseDto.getSchedules().getСреда();
                return writeScheduleOfDay(scheduleOfDay, weekNumber);
            }
            case "Четверг" -> {
                scheduleOfDay = responseDto.getSchedules().getЧетверг();
                return writeScheduleOfDay(scheduleOfDay, weekNumber);
            }
            case "Пятница" -> {
                scheduleOfDay = responseDto.getSchedules().getПятница();
                return writeScheduleOfDay(scheduleOfDay, weekNumber);
            }
            case "Суббота" -> {
                scheduleOfDay = responseDto.getSchedules().getСуббота();
                return writeScheduleOfDay(scheduleOfDay, weekNumber);
            }
            default -> {
                return null;
            }
        }
    }

    private String writeScheduleOfDay(List<Day> days, int weekNumber) {
        //ОБЩАЯ ПАРА
        String dayToSendFormatWithoutSubgroupsTemplate = """
                                
                Начало занятия: %s
                Сокращенное название предмета: %s
                Тип занятия: %s
                Аудитория: %s
                Конец занятия: %s 
                                
                """;
        //ПОДГРУППЫ
        String dayToSendFormatWithSubgroupsTemplate = """
                                
                Начало занятия: %s
                Сокращенное название предмета: %s
                Тип занятия: %s
                Подгруппа номер: %d
                Аудитория: %s
                Конец занятия: %s 
                                
                """;
        StringBuilder stringBuilder = new StringBuilder();
        days = days.stream().filter(d -> d.getWeekNumber().contains(weekNumber)).collect(Collectors.toList());
        //System.out.println(days);
        for (Day currDay : days) {
            System.out.println(currDay);
            String scheduleOfSubject = String.format(dayToSendFormatWithSubgroupsTemplate,
                    currDay.getStartLessonTime(),
                    currDay.getSubject(),
                    currDay.getLessonTypeAbbrev(),
                    currDay.getNumSubgroup(),
                    currDay.getAuditories().get(0),
                    currDay.getEndLessonTime()
                    );
            stringBuilder.append(scheduleOfSubject);
        }
        if (stringBuilder.length() == 0) {
            return "Ошибка";
        }
        return stringBuilder.toString();
    }


}
