package com.kseb.collabtool.domain.events.dto;

import com.kseb.collabtool.domain.events.entity.EventTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventTaskResponse {
    private Long id;
    private String title;
    private Long assigneeId;
    private String assigneeName;
    private Short statusId;
    private String statusCode;
    private String statusName;
    private LocalDateTime dueDatetime;

    public EventTaskResponse(EventTask task) {
        this.id = task.getId();
        this.title = task.getTitle();
        this.assigneeId = (task.getAssignee() != null) ? task.getAssignee().getId() : null; //null로 들어오면 service에서 본인으로 처리
        this.assigneeName = (task.getAssignee() != null) ? task.getAssignee().getName() : null;
        this.statusId = task.getTaskStatus().getId();
        this.statusCode = task.getTaskStatus().getCode();
        this.statusName = task.getTaskStatus().getName();
        this.dueDatetime = task.getDueDatetime();
    }
}
