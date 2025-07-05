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

    public GroupMemberResponse(GroupMember entity) {
        this.id = entity.getId();
        this.userId = entity.getUser().getId();
        this.userName = entity.getUser().getName();
        this.profileImg = entity.getUser().getProfileImg();
        this.roleCode = entity.getRole().getCode();
        this.roleName = entity.getRole().getName();
        this.joinedAt = entity.getJoinedAt();
    }
}

