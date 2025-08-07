package com.kseb.collabtool.domain.admin.dto;

import com.kseb.collabtool.domain.groups.entity.Group;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupAdminResponse {
    private Long id;
    private String name;
    private String createdBy;
    private int memberCount;
    private LocalDateTime createdAt;

    public static GroupAdminResponse from(Group group) {
        return GroupAdminResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .createdBy(group.getOwner().getName())
                .memberCount(group.getGroupMembers().size())
                .createdAt(group.getCreatedAt())
                .build();
    }
}
