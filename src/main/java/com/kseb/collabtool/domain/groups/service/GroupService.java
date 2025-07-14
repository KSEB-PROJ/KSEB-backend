package com.kseb.collabtool.domain.groups.service;

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

    private final EventRepository eventRepository;

    @Transactional
    public GroupResponse createGroup(GroupCreateRequest groupCreateRequest, User owner) {
        Group group = new Group();
        group.setName(groupCreateRequest.getName());
        group.setCode(generateInviteCode());
        group.setOwner(owner);
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

        //엔티티를 DTO 반환
        return GroupResponse.fromEntity(group);
    }
    //랜덤 방코드 생성
    private String generateInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    public List<GroupListDto> getGroupsByUser(Long userId) {
        return groupMemberRepository.findGroupsByUserId(userId);
    }

    @Transactional
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
        /*Long noticeChannelId = group.getNoticeChannel() != null ? group.getNoticeChannel().getId() : null;
        String noticeChannelName = group.getNoticeChannel() != null ? group.getNoticeChannel().getName() : null;*/

        //반환 DTO 구성
        return new GroupDetailDto(
                group.getId(),
                group.getName(),
                group.getCode(),
                //noticeChannelId,
                //noticeChannelName,
                members,
                members.size()
        );
    }

    @Transactional
    public void deleteGroupAndAllData(Long groupId, User currentUser) {
        //그룹 조회
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GeneralException(Status.GROUP_NOT_FOUND));

        //권한(오너) 검사
        if (!group.getOwner().getId().equals(currentUser.getId())) {
            throw new GeneralException(Status.GROUP_DELETE_FORBIDDEN);
        }


        //이벤트 등 외부 연관 데이터 직접 삭제 (ownerType/ownerId 조합)
        eventRepository.deleteByOwnerTypeAndOwnerId(OwnerType.GROUP, groupId);

        //삭제
        groupRepository.delete(group);
    }
}
