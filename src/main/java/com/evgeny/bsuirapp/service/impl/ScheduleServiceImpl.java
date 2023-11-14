package com.evgeny.bsuirapp.service.impl;

import com.evgeny.bsuirapp.models.json_models.Day;
import com.evgeny.bsuirapp.models.json_models.ScheduleResponseDto;
import com.evgeny.bsuirapp.service.ApiService;
import com.evgeny.bsuirapp.service.JsonParser;
import com.evgeny.bsuirapp.service.ScheduleService;
import com.evgeny.bsuirapp.util.DaysConverter;
import com.evgeny.bsuirapp.util.TemplatesForScheduleToSend;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        String response = null;
        int weekNumber = apiService.getNumberOfWeek();
        if (dayOption.equals("/today") || dayOption.equals("/tomorrow")) {
            String dayOfWeek = DaysConverter.getTodayOrTomorrow(dayOption);
            if (dayOfWeek.equals("Ошибка")) {
                return null;
            }
            response = parseScheduleResponseDto(dayOfWeek, weekNumber, scheduleResponseDto);
        } else if (dayOption.equals("/week")) {
            Map<String, List<Day>> dayWithScheduleMap = scheduleResponseDto.getSchedules().getAllDays();
            Map<String, List<Day>> mapWithWeek = filterMap(dayWithScheduleMap, weekNumber);
            return writeScheduleOfWeek(mapWithWeek);
        }
        if (response == null) {
            return "Расписание не было найдено :(";
        }
        return response;
    }

    private Map<String, List<Day>> filterMap(Map<String, List<Day>> map, int weekNumber) {
        Map<String, List<Day>> filteredMap = map.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .filter(day -> day.getWeekNumber().contains(weekNumber))
                                .collect(Collectors.toList())
                ));
        Map<String, List<Day>> filteredMapWithOrdering = new LinkedHashMap<>();
        filteredMapWithOrdering.put("Понедельник", filteredMap.get("Понедельник"));
        filteredMapWithOrdering.put("Вторник", filteredMap.get("Вторник"));
        filteredMapWithOrdering.put("Среда", filteredMap.get("Среда"));
        filteredMapWithOrdering.put("Четверг", filteredMap.get("Четверг"));
        filteredMapWithOrdering.put("Пятница", filteredMap.get("Пятница"));
        filteredMapWithOrdering.put("Суббота", filteredMap.get("Суббота"));
        return filteredMapWithOrdering;
        //return filteredMap;
    }

    private String parseScheduleResponseDto(String dayOfWeek, int weekNumber, ScheduleResponseDto responseDto) {
        List<Day> scheduleOfDay;
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

    private String writeScheduleOfWeek(Map<String, List<Day>> scheduleMap) {
        StringBuilder stringBuilder = new StringBuilder();
        for (var entry : scheduleMap.entrySet()) {
            if (entry.getValue() != null || !entry.getValue().isEmpty()) {
                stringBuilder.append("\n" + entry.getKey() + "\n");
                for (Day day : entry.getValue()) {
                    configureScheduleForStringBuilder(stringBuilder, day);
                }
            }
        }
        return stringBuilder.toString();
    }


    private String writeScheduleOfDay(List<Day> days, int weekNumber) {
        StringBuilder stringBuilder = new StringBuilder();
        days = days.stream().filter(d -> d.getWeekNumber().contains(weekNumber)).collect(Collectors.toList());
        for (Day currDay : days) {
            configureScheduleForStringBuilder(stringBuilder, currDay);
        }
        if (stringBuilder.length() == 0) {
            return "Ошибка";
        }
        return stringBuilder.toString();
    }

    private static void configureScheduleForStringBuilder(StringBuilder stringBuilder, Day currDay) {
        String scheduleOfSubject;
        if (currDay.getLessonTypeAbbrev().equals("Консультация") || currDay.getLessonTypeAbbrev().equals("Экзамен")) {
            return;
        }
        //Физра, без аудитории и общая группа
        if ((currDay.getAuditories() == null || currDay.getAuditories().isEmpty()) && currDay.getNumSubgroup() == 0) {
            scheduleOfSubject = String.format(TemplatesForScheduleToSend.subjectsWithoutAuditoriesTemplate(),
                    currDay.getStartLessonTime(),
                    currDay.getSubject(),
                    currDay.getLessonTypeAbbrev(),
                    currDay.getEndLessonTime()
            );
        } else if ((currDay.getAuditories() != null || !currDay.getAuditories().isEmpty()) && currDay.getNumSubgroup() == 0) {
            scheduleOfSubject = String.format(TemplatesForScheduleToSend.subjectsWithUnitedGroupsTemplate(),
                    currDay.getStartLessonTime(),
                    currDay.getSubject(),
                    currDay.getLessonTypeAbbrev(),
                    currDay.getAuditories().get(0),
                    currDay.getEndLessonTime()
            );
        } else {
            scheduleOfSubject = String.format(TemplatesForScheduleToSend.subjectsWithSubgroupsTemplate(),
                    currDay.getStartLessonTime(),
                    currDay.getSubject(),
                    currDay.getLessonTypeAbbrev(),
                    currDay.getNumSubgroup(),
                    currDay.getAuditories().get(0),
                    currDay.getEndLessonTime()
            );
        }
        if (scheduleOfSubject != null) {
            stringBuilder.append(scheduleOfSubject);
        }
    }
}
