package com.kseb.collabtool.domain.events.service;

import com.kseb.collabtool.domain.events.dto.EventCreateResult;
import com.kseb.collabtool.domain.events.dto.EventResponse;
import com.kseb.collabtool.domain.events.dto.EventUpdateRequest;
import com.kseb.collabtool.domain.events.dto.UserEventCreateRequest;
import com.kseb.collabtool.domain.events.entity.*;
import com.kseb.collabtool.domain.events.repository.EventParticipantRepository;
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
public class UserEventService {

    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;

    @Transactional
    public EventCreateResult createUserEvent(UserEventCreateRequest dto, Long userId) {
        boolean hasOverlap = eventRepository.existsOverlap( //겹치는 스케쥴 확인
                userId, dto.getStartDatetime(), dto.getEndDatetime());

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));
        Event event = new Event();
        event.setOwnerType(OwnerType.USER);
        event.setOwnerId(user.getId());
        event.setTitle(dto.getTitle());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setStartDatetime(dto.getStartDatetime());
        event.setEndDatetime(dto.getEndDatetime());
        event.setAllDay(dto.isAllDay());
        event.setRrule(dto.getRrule());
        event.setThemeColor(dto.getThemeColor());
        event.setCreatedBy(user);
        event.setUpdatedBy(user);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        eventRepository.save(event);

        // 참여자로 추가 (있어야되나???)
        EventParticipant self = new EventParticipant();
        self.setEvent(event);
        self.setUser(user);
        self.setStatus(ParticipantStatus.ACCEPTED);
        self.setId(new EventParticipantId(event.getId(), user.getId()));
        event.getParticipants().add(self);

        return new EventCreateResult(event.getId(), hasOverlap);
    }

    @Transactional
    public void deleteUserEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(Status.EVENT_NOT_FOUND));
        // 본인 소유 이벤트만 삭제 가능하게
        if (event.getOwnerType() != OwnerType.USER || !event.getOwnerId().equals(userId)) {
            throw new GeneralException(Status.NO_AUTHORITY); // 권한 없음 등
        }

        eventRepository.delete(event);
    }

    @Transactional
    public List<EventResponse> getAllEventsForUser(Long userId) {
        List<Event> events = eventRepository.findAllEventsForUser(userId);
        return events.stream()
                .map(EventResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateUserEvent(Long eventId, Long userId, EventUpdateRequest dto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(Status.EVENT_NOT_FOUND));
        //권한 체크
        if (!(event.getOwnerType() == OwnerType.USER && event.getOwnerId().equals(userId))) {
            throw new GeneralException(Status.NO_AUTHORITY);
        }

        // 프론트에서 바꾸고 싶은 값만 넘겨줌
        if (dto.getTitle() != null) event.setTitle(dto.getTitle());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getLocation() != null) event.setLocation(dto.getLocation());
        if (dto.getStartDatetime() != null) event.setStartDatetime(dto.getStartDatetime());
        if (dto.getEndDatetime() != null) event.setEndDatetime(dto.getEndDatetime());
        if (dto.getAllDay() != null) event.setAllDay(dto.getAllDay());
        if (dto.getRrule() != null) event.setRrule(dto.getRrule());
        if(dto.getThemeColor() != null) event.setThemeColor(dto.getThemeColor());

        // 변경자 변경일시 갱신
        User updater = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));
        event.setUpdatedBy(updater);
        event.setUpdatedAt(LocalDateTime.now());
    }
}


