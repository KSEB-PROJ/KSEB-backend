package com.kseb.collabtool.domain.events.dto;

import com.kseb.collabtool.domain.events.entity.Event;
import com.kseb.collabtool.domain.events.entity.OwnerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {
    private Long eventId;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private Boolean allDay;
    private String rrule;
    private OwnerType ownerType;
    private Long ownerId;
    private String themeColor;
    private List<EventParticipantDto> participants;



    public static EventResponse from(Event event) {
        List<EventParticipantDto> participants = event.getParticipants().stream()
                .map(EventParticipantDto::from)
                .collect(Collectors.toList());
        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getStartDatetime(),
                event.getEndDatetime(),
                event.getAllDay(),
                event.getRrule(),
                event.getOwnerType(),
                event.getOwnerId(),
                event.getThemeColor(),
                participants
        );
    }
}
