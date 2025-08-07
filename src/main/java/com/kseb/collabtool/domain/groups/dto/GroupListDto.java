package com.kseb.collabtool.domain.groups.dto;

import com.kseb.collabtool.domain.groups.entity.Group;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupListDto {
    private Long id;
    private String name;
    private String themeColor;

    public static GroupListDto fromEntity(Group group) {
        return new GroupListDto(
                group.getId(),
                group.getName(),
                group.getThemeColor()
        );
    }
}