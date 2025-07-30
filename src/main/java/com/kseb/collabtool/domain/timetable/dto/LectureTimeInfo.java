package com.kseb.collabtool.domain.timetable.dto;

import com.kseb.collabtool.domain.timetable.entity.CourseTimetable;
import lombok.Data;

import java.time.LocalTime;

@Data
public class LectureTimeInfo {
    private Long userId;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String rrule;

    public static LectureTimeInfo from(CourseTimetable courseTimetable) {
        LectureTimeInfo dto = new LectureTimeInfo();
        dto.setUserId(courseTimetable.getUser().getId());
        dto.setDayOfWeek(courseTimetable.getDayOfWeek().name());
        dto.setStartTime(courseTimetable.getStartTime());
        dto.setEndTime(courseTimetable.getEndTime());
        dto.setRrule(courseTimetable.getRrule());
        return dto;
    }
}
