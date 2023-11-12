package com.evgeny.bsuirapp.models.json_models;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StudentGroups {
    private String specialityName;
    private String specialityCode;
    private int numberOfStudents;
    private String name;
    private int educationDegree;
}
