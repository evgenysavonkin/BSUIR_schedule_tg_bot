package com.evgeny.bsuirapp.service.impl;

import com.evgeny.bsuirapp.service.ApiService;
import com.evgeny.bsuirapp.service.JsonParser;
import com.evgeny.bsuirapp.service.ScheduleService;
import com.evgeny.bsuirapp.models.json_models.ScheduleResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (obj instanceof ScheduleResponseDto){
            scheduleResponseDto = (ScheduleResponseDto) obj;
        }
        if (scheduleResponseDto == null){
            return "Не найдено!";
        }
        String formattedSchedule = """
                Группа номер: %d
                День недели: %s
                
                Аббревиатура: 
                
                
                
                """;
        switch (dayOption){
            case "/today" -> {


            }
            case "/tomorrow" -> {

            }
            case "/week" -> {

            }
        }

        return null;
        //System.out.println(scheduleResponseDto);
        //return scheduleResponseDto.toString();
    }

    public static void main(String[] args) {

    }
}
