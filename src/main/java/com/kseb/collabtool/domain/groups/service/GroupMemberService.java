package com.kseb.collabtool.domain.groups.service;

import com.kseb.collabtool.domain.groups.dto.GroupMemberResponse;
import com.kseb.collabtool.domain.groups.entity.GroupMember;
import com.kseb.collabtool.domain.groups.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;

    public List<GroupMemberResponse> getMembersByGroupId(Long groupId) {
        List<GroupMember> members = groupMemberRepository.findByGroup_Id(groupId);
        return members.stream()
                .map(GroupMemberResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
