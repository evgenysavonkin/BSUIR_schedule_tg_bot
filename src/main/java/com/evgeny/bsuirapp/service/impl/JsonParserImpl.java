package com.evgeny.bsuirapp.service.impl;

import com.evgeny.bsuirapp.service.JsonParser;
import com.evgeny.bsuirapp.models.json_models.ScheduleResponseDto;
import com.google.gson.Gson;
import org.springframework.stereotype.Service;

@Service
public class JsonParserImpl implements JsonParser {
    @Override
    public Object parseJson(String rawJson) {
        //GsonJsonParser gsonJsonParser = new GsonJsonParser();
        ScheduleResponseDto responseDto = new Gson().fromJson(rawJson, ScheduleResponseDto.class);
        if (responseDto == null){
            throw new RuntimeException();
        }
        System.out.println(responseDto);
        return responseDto;
    }
}
