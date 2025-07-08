package com.kseb.collabtool.domain.groups.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupListDto {
    private Long id;
    private String name;
    private String code;
    private Long noticeChannelId;
    private Long memberCount; //JPQL에서 COUNT(m)로 계산
}
