package com.kseb.collabtool.domain.admin.dto;

import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.entity.GroupMember;
import com.kseb.collabtool.domain.notice.entity.Notice;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class GroupDetailAdminResponse {

    private Long id;
    private String name;
    private String ownerName;
    private LocalDateTime createdAt;
    private int memberCount;
    private List<ChannelInfo> channels;
    private List<NoticeInfo> notices;
    private List<MemberInfo> members;

    @Getter
    @Builder
    public static class ChannelInfo {
        private Long id;
        private String name;
        private String type;

        public static ChannelInfo from(Channel channel) {
            return ChannelInfo.builder()
                    .id(channel.getId())
                    .name(channel.getName())
                    .type(channel.getChannelType().getName())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class NoticeInfo {
        private Long id;
        private String content;
        private String authorName;
        private LocalDateTime createdAt;

        public static NoticeInfo from(Notice notice) {
            return NoticeInfo.builder()
                    .id(notice.getId())
                    .content(notice.getContent())
                    .authorName(notice.getUser().getName())
                    .createdAt(notice.getCreatedAt())
                    .build();
        }
    }

    @Getter
    @Builder
    public static class MemberInfo {
        private Long userId;
        private String userName;
        private String role;
        private LocalDateTime joinedAt;

        public static MemberInfo from(GroupMember member) {
            return MemberInfo.builder()
                    .userId(member.getUser().getId())
                    .userName(member.getUser().getName())
                    .role(member.getRole().getName())
                    .joinedAt(member.getJoinedAt())
                    .build();
        }
    }

    public static GroupDetailAdminResponse from(Group group, List<Channel> channels, List<Notice> notices, List<GroupMember> members) {
        return GroupDetailAdminResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .ownerName(group.getOwner().getName())
                .createdAt(group.getCreatedAt())
                .memberCount(members.size())
                .channels(channels.stream().map(ChannelInfo::from).collect(Collectors.toList()))
                .notices(notices.stream().map(NoticeInfo::from).collect(Collectors.toList()))
                .members(members.stream().map(MemberInfo::from).collect(Collectors.toList()))
                .build();
    }
}
