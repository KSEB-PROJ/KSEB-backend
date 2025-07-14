package com.kseb.collabtool.domain.timetable.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kseb.collabtool.domain.timetable.entity.CourseTimetable;
import com.kseb.collabtool.domain.timetable.entity.DayOfWeek;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CourseTimetableDto {
    private Long id;
    private Long userId;    // 👈 추가 (User 연관관계면 필요)
    private String courseCode;
    private String courseName;
    private String professor;
    private String semester;
    private DayOfWeek dayOfWeek;
    @Schema(example = "09:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;
    @Schema(example = "12:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;
    private String location;
    private String rrule;

    public static CourseTimetableDto fromEntity(CourseTimetable timetable) {
        return CourseTimetableDto.builder()
                .id(timetable.getId())
                .userId(
                        timetable.getUser() != null ? timetable.getUser().getId() : null
                ) // 또는 timetable.getUserId()
                .courseCode(timetable.getCourseCode())
                .courseName(timetable.getCourseName())
                .professor(timetable.getProfessor())
                .semester(timetable.getSemester())
                .dayOfWeek(timetable.getDayOfWeek())
                .startTime(timetable.getStartTime())
                .endTime(timetable.getEndTime())
                .location(timetable.getLocation())
                .rrule(timetable.getRrule())
                .build();
    }

}
