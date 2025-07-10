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
    public EventCreateResult createGroupEvent(Long groupId,GroupEventCreateRequest dto, Long creatorId) {
        //그룹 일정에서 겹치는 스케줄이 있나 검사
        boolean hasOverlap = eventRepository.existsGroupOverlap(
                groupId, dto.getStartDatetime(), dto.getEndDatetime());

        // 그룹에 속한 모든 맴버 조회
        List<Long> userIds = groupMemberRepository.findUserIdsByGroupId(groupId);
        if (userIds.isEmpty()) throw new GeneralException(Status.GROUP_NOT_FOUND);

        //생성ㅇ자 조회
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));

        //그룹 이벤트 생성
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

        //그룹 이벤트 참석자(멤버 전원 추가)
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

        //각 멤버별 개인 이벤트 생성
        for (Long uid : userIds) {
            User member = userRepository.findById(uid)
                    .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));
            Event userEvent = new Event();
            userEvent.setOwnerType(OwnerType.USER);
            userEvent.setOwnerId(member.getId());
            userEvent.setTitle(dto.getTitle());
            userEvent.setDescription(dto.getDescription());
            userEvent.setLocation(dto.getLocation());
            userEvent.setStartDatetime(dto.getStartDatetime());
            userEvent.setEndDatetime(dto.getEndDatetime());
            userEvent.setAllDay(dto.isAllDay());
            userEvent.setRrule(dto.getRrule());
            userEvent.setCreatedBy(creator);
            userEvent.setUpdatedBy(creator);
            userEvent.setCreatedAt(LocalDateTime.now());
            userEvent.setUpdatedAt(LocalDateTime.now());
            userEvent.setGroupEventId(groupEvent.getId());

            eventRepository.save(userEvent);
            //모든 맴버 참석자 추가
            EventParticipant self = new EventParticipant();
            self.setEvent(userEvent);
            self.setUser(member); // 본인만!
            self.setStatus(ParticipantStatus.ACCEPTED);
            self.setId(new EventParticipantId(userEvent.getId(), member.getId()));
            userEvent.getParticipants().add(self);

            eventRepository.save(userEvent);
        }
        return new EventCreateResult(groupEvent.getId(), hasOverlap);
    }

    @Transactional
    public void deleteGroupEvent(Long groupId, Long eventId, Long userId) {
        // 이벤트 검증 있나없나
        Event groupEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(Status.EVENT_NOT_FOUND));

        // 그룹의 이벤트가 맞는지, 권한 체크
        if (groupEvent.getOwnerType() != OwnerType.GROUP || !groupEvent.getOwnerId().equals(groupId)) {
            throw new GeneralException(Status.NO_AUTHORITY);
        }

        //그룹에 속해있는 사람만 삭제 가능하게
        boolean isGroupMember = groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
        if (!isGroupMember) {
            throw new GeneralException(Status.NO_AUTHORITY);
        }

        //그룹장만 삭제 가능, 권한 체크도 가능 나중에 필요시

        // 그룹 이벤트 삭제
        eventRepository.delete(groupEvent);

        // groupEventId로 연결된 개인 일정들도 함께 삭제
        List<Event> linkedUserEvents = eventRepository.findAllByGroupEventId(groupEvent.getId());
        for (Event userEvent : linkedUserEvents) {
            eventRepository.delete(userEvent);
        }
    }

    @Transactional
    public List<EventResponseDto> getGroupEvents(Long groupId) {
        List<Event> events = eventRepository.findByOwnerTypeAndOwnerId(OwnerType.GROUP, groupId);
        return events.stream()
                .map(EventResponseDto::from)
                .collect(Collectors.toList());
    }

}
