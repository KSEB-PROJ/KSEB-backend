package com.kseb.collabtool.domain.groups.service;

import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.channel.entity.ChannelType;
import com.kseb.collabtool.domain.channel.repository.ChannelRepository;
import com.kseb.collabtool.domain.channel.repository.ChannelTypeRepository;
import com.kseb.collabtool.domain.events.entity.OwnerType;
import com.kseb.collabtool.domain.events.repository.EventRepository;
import com.kseb.collabtool.domain.groups.dto.GroupCreateRequest;
import com.kseb.collabtool.domain.groups.dto.GroupDetailDto;
import com.kseb.collabtool.domain.groups.dto.GroupListDto;
import com.kseb.collabtool.domain.groups.dto.GroupResponse;
import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.entity.GroupMember;
import com.kseb.collabtool.domain.groups.entity.MemberRole;
import com.kseb.collabtool.domain.groups.repository.GroupMemberRepository;
import com.kseb.collabtool.domain.groups.repository.GroupRepository;
import com.kseb.collabtool.domain.groups.repository.MemberRoleRepository;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.repository.UserRepository;
import com.kseb.collabtool.global.exception.GeneralException;
import com.kseb.collabtool.global.exception.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final EventRepository eventRepository;
    // 자동 채널 생성을 위한 Repository
    private final ChannelRepository channelRepository;
    private final ChannelTypeRepository channelTypeRepository;


    @Transactional
    public GroupResponse createGroup(GroupCreateRequest groupCreateRequest, User owner) {
        Group group = new Group();
        group.setName(groupCreateRequest.getName());
        group.setCode(generateInviteCode());
        group.setOwner(owner);
        group.setThemeColor(groupCreateRequest.getThemeColor()); // 테마 색상 설정
        group.setCreatedAt(LocalDateTime.now());
        group = groupRepository.save(group);

        MemberRole leaderRole = memberRoleRepository.findById((short)1) //리더는 1
                .orElseThrow(() -> new GeneralException(Status.MEMBER_ROLE_NOT_FOUND));

        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setUser(owner);
        groupMember.setRole(leaderRole);
        groupMember.setJoinedAt(LocalDateTime.now());
        groupMemberRepository.save(groupMember);

        // 기본 채널(공지사항, 일정) 자동 생성
        createDefaultChannels(group);

        return GroupResponse.fromEntity(group);
    }

    // 기본 채널 생성 로직
    private void createDefaultChannels(Group group) {
        // 공지사항 채널
        ChannelType noticeType = channelTypeRepository.findById((short) 3)
                .orElseThrow(() -> new GeneralException(Status.CHANNEL_TYPE_NOT_FOUND, "공지 채널 타입을 찾을 수 없습니다."));
        Channel noticeChannel = new Channel();
        noticeChannel.setGroup(group);
        noticeChannel.setName("공지사항");
        noticeChannel.setChannelType(noticeType);
        noticeChannel.setIsSystem(true);
        channelRepository.save(noticeChannel);

        // 캘린더 채널
        ChannelType calendarType = channelTypeRepository.findById((short) 2)
                .orElseThrow(() -> new GeneralException(Status.CHANNEL_TYPE_NOT_FOUND, "캘린더 채널 타입을 찾을 수 없습니다."));
        Channel calendarChannel = new Channel();
        calendarChannel.setGroup(group);
        calendarChannel.setName("일정");
        calendarChannel.setChannelType(calendarType);
        calendarChannel.setIsSystem(true);
        channelRepository.save(calendarChannel);
    }

    //랜덤 방코드 생성
    private String generateInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    public List<GroupListDto> getGroupsByUser(Long userId) {
        List<Group> groups = groupMemberRepository.findGroupsByUserId(userId);
        return groups.stream()
                .map(GroupListDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public GroupDetailDto getGroupDetail(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GeneralException(Status.GROUP_NOT_FOUND));

        List<GroupMember> groupMembers = groupMemberRepository.findByGroupId(groupId);

        List<GroupDetailDto.MemberInfo> members = groupMembers.stream()
                .map(gm -> new GroupDetailDto.MemberInfo(
                        gm.getUser().getId(),
                        gm.getUser().getName(),
                        gm.getRole().getCode()
                )).toList();

        return new GroupDetailDto(
                group.getId(),
                group.getName(),
                group.getCode(),
                members,
                members.size()
        );
    }

    @Transactional
    public void deleteGroupAndAllData(Long groupId, User currentUser) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GeneralException(Status.GROUP_NOT_FOUND));

        if (!group.getOwner().getId().equals(currentUser.getId())) {
            throw new GeneralException(Status.GROUP_DELETE_FORBIDDEN);
        }

        eventRepository.deleteByOwnerTypeAndOwnerId(OwnerType.GROUP, groupId);
        groupRepository.delete(group);
    }
}
