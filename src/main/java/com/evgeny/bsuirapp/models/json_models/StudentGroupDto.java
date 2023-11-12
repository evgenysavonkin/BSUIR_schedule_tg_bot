package com.evgeny.bsuirapp.models.json_models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StudentGroupDto {
    private String name;
    private int facultyId;
    private String facultyAbbrev;
    private int specialityDepartmentEducationFormId;
    private String specialityName;
    private String specialityAbbrev;
    private int course;
    private int id;
    private String calendarId;
    private int educationDegree;
}
