package com.kseb.collabtool.domain.groups.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class GroupDetailDto {
    private Long id;
    private String name;
    private String code;
    /*private Long noticeChannelId;
    private String noticeChannelName;*/
    private List<MemberInfo> members;
    private int memberCount;

    @Data
    @AllArgsConstructor
    public static class MemberInfo { //그룹에 속한 유저 정보들
        private Long userId;
        private String userName;
        private String role;
    }
}
