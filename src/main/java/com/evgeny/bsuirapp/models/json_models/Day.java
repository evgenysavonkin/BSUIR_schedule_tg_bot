package com.evgeny.bsuirapp.models.json_models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class Day {
    private List<String> auditories;
    private String endLessonTime;
    private String lessonTypeAbbrev;
    private String note;
    private int numSubgroup;
    private String startLessonTime;
    private List<StudentGroups> studentGroups;
    private String subject;
    private String subjectFullName;
    private List<Integer> weekNumber;
    private List<Employee> employees;
    private String dateLesson;
    private String startLessonDate;
    private String endLessonDate;
    private boolean announcement;
    private boolean split;
}
