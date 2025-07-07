package com.kseb.collabtool.domain.groups.service;

import com.kseb.collabtool.domain.groups.dto.GroupCreateRequest;
import com.kseb.collabtool.domain.groups.dto.GroupDetailDto;
import com.kseb.collabtool.domain.groups.dto.GroupListDto;
import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.entity.GroupMember;
import com.kseb.collabtool.domain.groups.entity.MemberRole;
import com.kseb.collabtool.domain.groups.repository.GroupMemberRepository;
import com.kseb.collabtool.domain.groups.repository.MemberRoleRepository;
import com.kseb.collabtool.domain.groups.repository.GroupRepository;
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

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    private final GroupMemberRepository groupMemberRepository;

    private final MemberRoleRepository memberRoleRepository;

    @Transactional
    public Group createGroup(GroupCreateRequest groupCreateRequest, User owner) {
        Group group = new Group();
        group.setName(groupCreateRequest.getName());
        group.setCode(generateInviteCode());
        group.setOwner(owner);
        group.setCreatedAt(LocalDateTime.now());
        group = groupRepository.save(group);

        MemberRole leaderRole = memberRoleRepository.findById((short)1) //리더는 1
                .orElseThrow(() -> new GeneralException(Status.MEMBER_ROLE_NOT_FOUND));

        // 생성자를 group_members에 추가
        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setUser(owner);
        groupMember.setRole(leaderRole);
        groupMember.setJoinedAt(LocalDateTime.now());
        groupMemberRepository.save(groupMember);

        return group;
    }
    //랜덤 방코드 생성
    private String generateInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    public List<GroupListDto> getGroupsByUser(Long userId) {
        return groupMemberRepository.findGroupsByUserId(userId);
    }

    public GroupDetailDto getGroupDetail(Long groupId) {
        //그룹 정보 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GeneralException(Status.GROUP_NOT_FOUND));

        //멤버 목록 조회
        List<GroupMember> groupMembers = groupMemberRepository.findByGroup_Id(groupId);

        List<GroupDetailDto.MemberInfo> members = groupMembers.stream()
                .map(gm -> new GroupDetailDto.MemberInfo(
                        gm.getUser().getId(),
                        gm.getUser().getName(),
                        gm.getRole().getCode()
                )).toList();

        // 공지채널 정보 ??????
        Long noticeChannelId = group.getNoticeChannel() != null ? group.getNoticeChannel().getId() : null;
        String noticeChannelName = group.getNoticeChannel() != null ? group.getNoticeChannel().getName() : null;

        //반환 DTO 구성
        return new GroupDetailDto(
                group.getId(),
                group.getName(),
                group.getCode(),
                noticeChannelId,
                noticeChannelName,
                members,
                members.size()
        );
    }

    @Transactional
    public void deleteGroup(Long groupId, User currentUser) {
        //그룹 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GeneralException(Status.GROUP_NOT_FOUND));

        //권한(오너) 검사
        if (!group.getOwner().getId().equals(currentUser.getId())) {
            throw new GeneralException(Status.GROUP_DELETE_FORBIDDEN);
        }
        //삭제
        groupRepository.delete(group);
    }
}
