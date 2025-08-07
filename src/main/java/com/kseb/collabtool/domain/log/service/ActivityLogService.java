package com.kseb.collabtool.domain.log.service;

import com.kseb.collabtool.domain.log.entity.ActionType;
import com.kseb.collabtool.domain.log.entity.ActivityLog;
import com.kseb.collabtool.domain.log.repository.ActivityLogRepository;
import com.kseb.collabtool.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    /**
     * 활동 로그를 저장합니다.
     * @param actor 활동을 수행한 사용자 (시스템 활동의 경우 null)
     * @param actionType 활동 유형
     * @param targetId 활동 대상의 ID (선택 사항)
     * @param details 추가 정보 (선택 사항)
     */
    public void saveLog(User actor, ActionType actionType, Long targetId, String details) {
        ActivityLog log = ActivityLog.builder()
                .actor(actor)
                .actionType(actionType)
                .targetId(targetId)
                .details(details)
                .build();
        activityLogRepository.save(log);
    }

    // 오버로딩을 통해 다양한 케이스를 처리
    public void saveLog(User actor, ActionType actionType, Long targetId) {
        saveLog(actor, actionType, targetId, null);
    }

    public void saveLog(User actor, ActionType actionType) {
        saveLog(actor, actionType, null, null);
    }
}
