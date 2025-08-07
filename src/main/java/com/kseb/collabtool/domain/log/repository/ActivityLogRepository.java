package com.kseb.collabtool.domain.log.repository;

import com.kseb.collabtool.domain.log.entity.ActionType;
import com.kseb.collabtool.domain.log.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long>, JpaSpecificationExecutor<ActivityLog> {
    // ActionType을 기준으로 로그를 필터링하여 조회 (Specification으로 대체되므로 주석 처리 또는 삭제 가능)
    // Page<ActivityLog> findByActionType(ActionType actionType, Pageable pageable);
}
