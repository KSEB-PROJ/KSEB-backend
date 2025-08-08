// kdae/src - back/main/java/com/kseb/collabtool/domain/admin/dto/LogResponse.java
package com.kseb.collabtool.domain.admin.dto;

import com.kseb.collabtool.domain.log.entity.ActionType;
import com.kseb.collabtool.domain.log.entity.ActivityLog;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LogResponse {
    private Long id;
    private String actorName;
    private ActionType actionType;
    private String actionDescription;
    private Long targetId;
    private String details;
    private LocalDateTime createdAt;

    // 상세 정보를 위한 필드 추가
    private String groupName;
    private String channelName;
    private String targetContent;

    // from 메서드는 AdminService에서 상세 정보를 채우는 방식으로 대체됩니다.
    public static LogResponse from(ActivityLog log) {
        return LogResponse.builder()
                .id(log.getId())
                .actorName(log.getActor() != null ? log.getActor().getName() : "System")
                .actionType(log.getActionType())
                .actionDescription(log.getActionType().getDescription())
                .targetId(log.getTargetId())
                .details(log.getDetails())
                .createdAt(log.getCreatedAt())
                .build();
    }
}