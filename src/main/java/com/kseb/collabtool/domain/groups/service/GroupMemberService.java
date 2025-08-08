// kseb-proj/kseb-backend/KSEB-backend-feature-log/src/main/java/com/kseb/collabtool/domain/groups/service/GroupMemberService.java

package com.kseb.collabtool.domain.groups.service;

import com.kseb.collabtool.domain.groups.dto.GroupMemberResponse;
import com.kseb.collabtool.domain.groups.dto.GroupResponse;
import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.entity.GroupMember;
import com.kseb.collabtool.domain.groups.entity.MemberRole;
import com.kseb.collabtool.domain.groups.repository.GroupMemberRepository;
import com.kseb.collabtool.domain.groups.repository.GroupRepository;
import com.kseb.collabtool.domain.groups.repository.MemberRoleRepository;
import com.kseb.collabtool.domain.log.entity.ActionType; // [추가]
import com.kseb.collabtool.domain.log.service.ActivityLogService; // [추가]
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.global.exception.GeneralException;
import com.kseb.collabtool.global.exception.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;
    private final GroupRepository groupRepository;
    private final MemberRoleRepository memberRoleRepository;
    private final ActivityLogService activityLogService; // [추가]

    public List<GroupMemberResponse> getMembersByGroupId(Long groupId) {
        // 그룹(방) 존재 여부 체크
        boolean exists = groupRepository.existsById(groupId);
        if (!exists) {
            throw new GeneralException(Status.GROUP_NOT_FOUND);
        }

        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
        return members.stream()
                .map(GroupMemberResponse::fromEntity)
                .collect(Collectors.toList());
    }

    //그룹 참가
    @Transactional
    public GroupResponse joinGroupByInviteCode(String inviteCode, User user) {
        Group group = groupRepository.findByCode(inviteCode) //초대코드검사
                .orElseThrow(() -> new GeneralException(Status.INVALID_INVITE_CODE));

        // 이미 멤버인지 체크
        if (groupMemberRepository.existsByGroupIdAndUserId(group.getId(), user.getId())) {
            throw new GeneralException(Status.MEMBER_ALREADY_JOINED);
        }

        MemberRole role = memberRoleRepository.findById((short)2)
                .orElseThrow(() -> new GeneralException(Status.MEMBER_ROLE_NOT_FOUND));

        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setUser(user);
        member.setRole(role);
        member.setJoinedAt(LocalDateTime.now());
        groupMemberRepository.save(member);

        // [추가] 그룹 참가 로그를 기록합니다.
        activityLogService.saveLog(user, ActionType.GROUP_JOIN_USER, group.getId(), "Joined with code: " + inviteCode);

        return GroupResponse.fromEntity(group);
    }
}