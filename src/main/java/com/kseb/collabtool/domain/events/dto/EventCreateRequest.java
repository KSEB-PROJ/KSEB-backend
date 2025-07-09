package com.kseb.collabtool.domain.events.dto;

import com.kseb.collabtool.domain.events.entity.Event;
import com.kseb.collabtool.domain.events.entity.OwnerType;
import com.kseb.collabtool.domain.user.entity.User;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class EventCreateRequest {
    private OwnerType ownerType; // USER, GROUP
    private Long ownerId; // 유저 or 그룹 id
    private String title;
    private String description;
    private String location;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Boolean allDay;
    private String rrule;
    private List<EventTaskDto> tasks; // 할일 정보(Optional)
    private List<Long> participantIds; // 참여자(userId) 목록

    public Event toEntity(User createdBy) {
        return new Event(
                null, this.ownerType, this.ownerId, this.title, this.description,
                this.location, this.startDatetime, this.endDatetime, this.allDay, this.rrule,
                createdBy, null, null, null, new ArrayList<>()
        );
    }
}
