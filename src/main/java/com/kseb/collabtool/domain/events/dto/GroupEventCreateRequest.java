package com.kseb.collabtool.domain.events.dto;

import com.kseb.collabtool.domain.events.entity.Event;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GroupEventCreateRequest {
    private String title;
    private String description;
    private String location;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private boolean allDay;
    private String rrule;
    private List<Long> participantIds;

    public Event toEntity() {
        Event event = new Event();
        event.setTitle(this.title);
        event.setDescription(this.description);
        event.setLocation(this.location);
        event.setStartDatetime(this.startDatetime);
        event.setEndDatetime(this.endDatetime);
        event.setAllDay(this.allDay);
        event.setRrule(this.rrule);
        // createdBy, updatedBy 등은 여기서 못 넣음
        return event;
    }

}
