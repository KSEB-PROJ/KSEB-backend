package com.kseb.collabtool.domain.groups.dto;

import com.kseb.collabtool.domain.groups.entity.Group;
import lombok.Data;

@Data
public class GroupResponse {
    private Long id;
    private String name;
    private String code;
    private String ownerName;
    private String profileImg;

    public static GroupResponse fromEntity(Group group) {
        if (group == null) return null;
        GroupResponse dto = new GroupResponse();
        dto.id = group.getId();
        dto.name = group.getName();
        dto.code = group.getCode();
        dto.ownerName = group.getOwner().getName();
        dto.profileImg = group.getOwner().getProfileImg();
        return dto;
    }
}

