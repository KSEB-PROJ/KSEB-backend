package com.kseb.collabtool.domain.groups.dto;

import com.kseb.collabtool.domain.groups.entity.GroupMember;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMemberResponse { 
    private Long id;
    private Long userId;
    private String userName;
    private String profileImg;
    private String roleCode;
    private String roleName;
    private LocalDateTime joinedAt;

    public static GroupMemberResponse fromEntity(GroupMember entity) {
        if (entity == null) return null;
        return new GroupMemberResponse(
                entity.getId(),
                entity.getUser().getId(),
                entity.getUser().getName(),
                entity.getUser().getProfileImg(),
                entity.getRole().getCode(),
                entity.getRole().getName(),
                entity.getJoinedAt()
        );
    }
}

