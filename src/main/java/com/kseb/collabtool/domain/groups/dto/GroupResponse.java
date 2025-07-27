package com.kseb.collabtool.domain.groups.dto;

import com.kseb.collabtool.domain.groups.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class GroupResponse {
    private Long id;
    private String name;
    private String code;
    private Long ownerId;
    private String ownerName;
    private String themeColor;
    private LocalDateTime createdAt;

    public static GroupResponse fromEntity(Group group) {
        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getCode(),
                group.getOwner().getId(),
                group.getOwner().getName(),
                group.getThemeColor(),
                group.getCreatedAt()
        );
    }
}


