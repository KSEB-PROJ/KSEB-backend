package com.kseb.collabtool.domain.events.repository;


import com.kseb.collabtool.domain.events.entity.EventTask;
import com.kseb.collabtool.domain.events.entity.OwnerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventTaskRepository extends JpaRepository<EventTask, Long> {
    List<EventTask> findByEvent_Id(Long eventId);
    // 한 유저가 assignee인 모든 Task 조회
    List<EventTask> findByAssignee_Id(Long assigneeId);

    // groupId에 속한 모든 Task 조회 (이벤트 ownerType이 GROUP이고 ownerId가 groupId인 이벤트에 속한 할 일)
    @Query("SELECT t FROM EventTask t WHERE t.event.ownerType = :ownerType AND t.event.ownerId = :groupId")
    List<EventTask> findByGroupId(@Param("ownerType") OwnerType ownerType, @Param("groupId") Long groupId);

    @Modifying
    @Query("UPDATE EventTask et SET et.assignee = null WHERE et.assignee.id = :userId")
    void unassignUserFromTasks(@Param("userId") Long userId);
}
