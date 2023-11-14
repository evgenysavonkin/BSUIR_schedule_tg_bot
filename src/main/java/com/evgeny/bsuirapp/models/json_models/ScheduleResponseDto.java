package com.evgeny.bsuirapp.models.json_models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScheduleResponseDto {
    @JsonFormat(pattern = "dd.MM.yyyy")
    private String startDate;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private String endDate;
    private String startExamsDate;
    private String endExamsDate;
    private String employeeDto;
    private StudentGroupDto studentGroupDto;
    private Schedule schedules;
}
