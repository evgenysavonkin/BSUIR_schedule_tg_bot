package com.evgeny.bsuirapp.service.impl;

import com.evgeny.bsuirapp.service.ApiService;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiServiceImpl implements ApiService {
    private static final RestTemplate restTemplate;
    private static final String GET_SCHEDULE_BY_GROUP_LINK_PATTERN = "https://iis.bsuir.by/api/v1/schedule?studentGroup=%d";
    private static final String GET_WEEK_NUMBER_LINK = "https://iis.bsuir.by/api/v1/schedule/current-week";

    static {
        restTemplate = new RestTemplate();
    }

    @Override
    public String getRawScheduleJson(int groupId) {
        String schedule_link = String.format(GET_SCHEDULE_BY_GROUP_LINK_PATTERN, groupId);
        String jsonResponse = restTemplate.getForObject(schedule_link, String.class);
        if (jsonResponse == null || jsonResponse.length() == 0) {
            //TODO: Реализовать отлов ошибок и их обработку
            return "Произошла ошибка :(";
        }
        return jsonResponse;
    }

    @Override
    public int getNumberOfWeek() {
        int numberOfWeek = restTemplate.getForObject(GET_WEEK_NUMBER_LINK, Integer.class);
        if (!(numberOfWeek > 0 && numberOfWeek <= 4)) {
            throw new RuntimeException();
        }
        return numberOfWeek;
    }
}
