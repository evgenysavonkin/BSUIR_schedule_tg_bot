package com.evgeny.bsuirapp.models.json_models;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Employee {
    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String photoLink;
    private String degree;
    private String degreeAbbrev;
    private String rank;
    private String email;
    private String urlId;
    private String calendarId;
    private String jobPositions;
}
