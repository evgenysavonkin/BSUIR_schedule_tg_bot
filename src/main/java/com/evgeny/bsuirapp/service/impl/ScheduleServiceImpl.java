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
    public String getFormattedScheduleByUserGroup(int groupId) {
        String rawJson = apiService.getRawScheduleJson(groupId);
        Object obj = jsonParser.parseJson(rawJson);
        ScheduleResponseDto scheduleResponseDto;
        if (obj instanceof ScheduleResponseDto){
            scheduleResponseDto = (ScheduleResponseDto) obj;
        }

        return null;
    }

    public static void main(String[] args) {

    }
}
