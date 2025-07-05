package com.kseb.collabtool.domain.groups.service;

import com.kseb.collabtool.domain.groups.dto.GroupCreateRequest;
import com.kseb.collabtool.domain.groups.dto.GroupMemberRequest;
import com.kseb.collabtool.domain.groups.dto.GroupMemberResponse;
import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.entity.GroupMember;
import com.kseb.collabtool.domain.groups.entity.MemberRole;
import com.kseb.collabtool.domain.groups.repository.GroupMemberRepository;
import com.kseb.collabtool.domain.groups.repository.GroupRepository;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;

    private final UserRepository userRepository;

    private final GroupMemberRepository groupMemberRepository;

    public Group createGroup(GroupCreateRequest groupCreateRequest, User owner) {
        Group group = new Group();
        group.setName(groupCreateRequest.getName());
        group.setCode(generateInviteCode());
        group.setOwner(owner);
        group.setCreatedAt(LocalDateTime.now());
        return groupRepository.save(group);
    }

    private String generateInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }


   /* // 그룹 참여(가입) - 항상 MEMBER 역할
    public GroupMemberResponse joinGroup(Long groupId, GroupMemberRequest request) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("그룹 없음"));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        // 이미 가입된 멤버인지 중복체크 (중복 가입 방지)
        groupMemberRepository.findByGroupAndUser(group, user)
                .ifPresent(gm -> { throw new IllegalStateException("이미 가입한 유저입니다."); });

        // 역할은 항상 "MEMBER"
        MemberRole role = memberRoleRepository.findByCode("MEMBER")
                .orElseThrow(() -> new IllegalArgumentException("MEMBER 역할 없음"));

        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setUser(user);
        groupMember.setRole(role);
        groupMember.setJoinedAt(LocalDateTime.now());

        groupMemberRepository.save(groupMember);

        return new GroupMemberResponse(groupMember);
    }*/
}
