package com.kseb.collabtool.domain.events.dto;

import com.kseb.collabtool.domain.timetable.dto.LectureTimeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GroupScheduleBundle {
    private List<EventTimeInfoResponse> personalEvents;
    private List<EventTimeInfoResponse> groupEvents;
    private List<LectureTimeInfo> lectures;
}
