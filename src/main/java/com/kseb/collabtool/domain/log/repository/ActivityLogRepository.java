package com.kseb.collabtool.domain.log.repository;

import com.kseb.collabtool.domain.log.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
}
