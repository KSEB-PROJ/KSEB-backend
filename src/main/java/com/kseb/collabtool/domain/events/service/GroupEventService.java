package com.kseb.collabtool.domain.events.service;

import com.kseb.collabtool.domain.events.dto.EventCreateResult;
import com.kseb.collabtool.domain.events.dto.EventResponseDto;
import com.kseb.collabtool.domain.events.dto.GroupEventCreateRequest;
import com.kseb.collabtool.domain.events.entity.*;
import com.kseb.collabtool.domain.events.repository.EventRepository;
import com.kseb.collabtool.domain.groups.repository.GroupMemberRepository;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.repository.UserRepository;
import com.kseb.collabtool.global.exception.GeneralException;
import com.kseb.collabtool.global.exception.Status;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupEventService {

    private final EventRepository eventRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Transactional
    public EventCreateResult createGroupEvent(Long groupId, GroupEventCreateRequest dto, Long creatorId) {
        //겹침 검사
        boolean hasOverlap = eventRepository.existsGroupOverlap(
                groupId, dto.getStartDatetime(), dto.getEndDatetime());

        //그룹에 속한 멤버 조회
        List<Long> userIds = groupMemberRepository.findUserIdsByGroupId(groupId);
        if (userIds.isEmpty()) throw new GeneralException(Status.GROUP_NOT_FOUND);

        //생성자 조회
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));

        //그룹 일정 생성 (오직 1개만)
        Event groupEvent = new Event();
        groupEvent.setOwnerType(OwnerType.GROUP);
        groupEvent.setOwnerId(groupId);
        groupEvent.setTitle(dto.getTitle());
        groupEvent.setDescription(dto.getDescription());
        groupEvent.setLocation(dto.getLocation());
        groupEvent.setStartDatetime(dto.getStartDatetime());
        groupEvent.setEndDatetime(dto.getEndDatetime());
        groupEvent.setAllDay(dto.isAllDay());
        groupEvent.setRrule(dto.getRrule());
        groupEvent.setCreatedBy(creator);
        groupEvent.setUpdatedBy(creator);
        groupEvent.setCreatedAt(LocalDateTime.now());
        groupEvent.setUpdatedAt(LocalDateTime.now());

        eventRepository.save(groupEvent);

        //참석자(그룹 멤버 전원) 등록
        for (Long uid : userIds) {
            User member = userRepository.findById(uid)
                    .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));
            EventParticipant participant = new EventParticipant();
            participant.setEvent(groupEvent);
            participant.setUser(member);
            participant.setStatus(ParticipantStatus.ACCEPTED);
            participant.setId(new EventParticipantId(groupEvent.getId(), member.getId()));
            groupEvent.getParticipants().add(participant);
        }
        eventRepository.save(groupEvent);

        return new EventCreateResult(groupEvent.getId(), hasOverlap);
    }

    @Transactional
    public void deleteGroupEvent(Long groupId, Long eventId, Long userId) {
        //이벤트 존재
        Event groupEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(Status.EVENT_NOT_FOUND));

        if (groupEvent.getOwnerType() != OwnerType.GROUP || !groupEvent.getOwnerId().equals(groupId)) {
            throw new GeneralException(Status.NO_AUTHORITY);
        }
        //그룹 속해 있나
        boolean isGroupMember = groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
        if (!isGroupMember) {
            throw new GeneralException(Status.NO_AUTHORITY);
        }

        // 그룹 이벤트 삭제
        eventRepository.delete(groupEvent);
    }

    @Transactional
    public List<EventResponseDto> getGroupEvents(Long groupId) {
        List<Event> events = eventRepository.findByOwnerTypeAndOwnerId(OwnerType.GROUP, groupId);
        return events.stream()
                .map(EventResponseDto::from)
                .collect(Collectors.toList());
    }
}
