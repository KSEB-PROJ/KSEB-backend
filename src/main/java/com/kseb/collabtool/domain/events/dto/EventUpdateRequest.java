package com.kseb.collabtool.domain.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.kseb.collabtool.global.validation.ValidRRule;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class EventUpdateRequest {
    private String title;
    private String description;
    private String location;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") //날짜와 시간 형식 명시해야함
    private LocalDateTime startDatetime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDatetime;
    private Boolean allDay;
    @ValidRRule
    private String rrule;
    private String themeColor;
}
