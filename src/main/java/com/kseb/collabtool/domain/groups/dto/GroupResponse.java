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

    public GroupResponse(Group group) {
        this.id = group.getId();
        this.name = group.getName();
        this.code = group.getCode();
        this.ownerName = group.getOwner().getName();
        this.profileImg = group.getOwner().getProfileImg();
    }
}

