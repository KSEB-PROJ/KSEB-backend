package com.kseb.collabtool.domain.events.dto;

import com.kseb.collabtool.domain.events.entity.Event;
import com.kseb.collabtool.domain.events.entity.EventTask;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponse { // 내가 해야되는 Task 전체를 보여줌
    private Long id;
    private String title;
    private LocalDateTime dueDatetime;
    private Short statusId;
    private String statusCode;
    private String statusName;
    // 이벤트 정보도 다 넘겨줌
    private Long eventId;
    private String eventTitle;
    private String eventOwnerType;
    private Long eventOwnerId;
    private LocalDateTime eventStartDatetime;
    private LocalDateTime eventEndDatetime;
    //한 유저의 모든 event(일정)과 task(할 일)을 보여줌
    public TaskResponse(EventTask task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.dueDatetime = task.getDueDatetime();
        this.statusId = task.getTaskStatus().getId();
        this.statusCode = task.getTaskStatus().getCode();
        this.statusName = task.getTaskStatus().getName();

        // 이벤트 정보 추가
        Event event = task.getEvent();
        this.eventId = event.getId();
        this.eventTitle = event.getTitle();
        this.eventOwnerType = event.getOwnerType().name();
        this.eventOwnerId = event.getOwnerId();
        this.eventStartDatetime = event.getStartDatetime();
        this.eventEndDatetime = event.getEndDatetime();
    }
}
