package com.kseb.collabtool.domain.groups.service;

import com.kseb.collabtool.domain.groups.dto.GroupMemberResponse;
import com.kseb.collabtool.domain.groups.dto.GroupResponse;
import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.entity.GroupMember;
import com.kseb.collabtool.domain.groups.entity.MemberRole;
import com.kseb.collabtool.domain.groups.repository.GroupMemberRepository;
import com.kseb.collabtool.domain.groups.repository.GroupRepository;
import com.kseb.collabtool.domain.groups.repository.MemberRoleRepository;
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

    public List<GroupMemberResponse> getMembersByGroupId(Long groupId) {
        // 그룹(방) 존재 여부 체크
        boolean exists = groupRepository.existsById(groupId);
        if (!exists) {
            throw new GeneralException(Status.GROUP_NOT_FOUND);
        }

        List<GroupMember> members = groupMemberRepository.findByGroup_Id(groupId);
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

        return GroupResponse.fromEntity(group);
    }


    /*
    @Transactional
    public void changeRole(Long groupId, Long memberId, User user) {
        //LEADER인지 검증
        GroupMember operatorMember = groupMemberRepository.findByGroupIdAndUserId(groupId, user.getId())
                .orElseThrow(() -> new IllegalStateException("그룹의 멤버가 아님"));
        if (!"LEADER".equals(operatorMember.getRole().getCode())) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        //대상 멤버 조회하고 역할 변경ㅎ,기
        GroupMember targetMember = groupMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("멤버가 존재하지 않습니다."));
        MemberRole newRole = memberRoleRepository.findByCode(roleCode)
                .orElseThrow(() -> new IllegalArgumentException("역할 코드가 잘못되었습니다."));

        targetMember.setRole(newRole);
    }
    */

    //리
}
