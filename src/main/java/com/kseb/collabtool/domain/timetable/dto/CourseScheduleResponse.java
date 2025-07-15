package com.kseb.collabtool.domain.timetable.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CourseScheduleResponse {
    private Long eventId;
    private boolean hasOverlap;
}
