package com.kseb.collabtool.domain.events.repository;


import com.kseb.collabtool.domain.events.entity.EventTask;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventTaskRepository extends JpaRepository<EventTask, Long> {
    List<EventTask> findByEvent_Id(Long eventId);
    // 한 유저가 assignee인 모든 Task 조회
    List<EventTask> findByAssignee_Id(Long assigneeId);
}
