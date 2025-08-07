package com.kseb.collabtool.domain.log.repository;

import com.kseb.collabtool.domain.log.entity.ActionType;
import com.kseb.collabtool.domain.log.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long>, JpaSpecificationExecutor<ActivityLog> {
    
    @Modifying
    @Query("UPDATE ActivityLog al SET al.actor = null WHERE al.actor.id = :userId")
    void nullifyActorForUser(@Param("userId") Long userId);
}