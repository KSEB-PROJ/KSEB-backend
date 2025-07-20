package com.kseb.collabtool.domain.events.dto;

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
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Boolean allDay;
    @ValidRRule
    private String rrule;
    private String themeColor;
}
