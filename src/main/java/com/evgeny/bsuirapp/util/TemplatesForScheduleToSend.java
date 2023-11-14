package com.evgeny.bsuirapp.util;

public class TemplatesForScheduleToSend {
    private TemplatesForScheduleToSend(){

    }

    public static String subjectsWithoutAuditoriesTemplate(){
        return """
                                
                Начало занятия: %s
                Сокращенное название предмета: %s
                Тип занятия: %s
                Конец занятия: %s 
                                
                """;
    }

    public static String subjectsWithUnitedGroupsTemplate(){
        return """
                                
                Начало занятия: %s
                Сокращенное название предмета: %s
                Тип занятия: %s
                Аудитория: %s
                Конец занятия: %s
                                               
                """;
    }

    public static String subjectsWithSubgroupsTemplate(){
        return """
                                
                Начало занятия: %s
                Сокращенное название предмета: %s
                Тип занятия: %s
                Подгруппа номер: %d
                Аудитория: %s
                Конец занятия: %s
                                
                """;
    }
}
