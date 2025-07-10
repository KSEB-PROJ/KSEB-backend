package com.kseb.collabtool.domain.events.service;

import com.kseb.collabtool.domain.events.dto.EventCreateResult;
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

@Service
@RequiredArgsConstructor
public class EventService {

    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventParticipantRepository eventParticipantRepository;

    @Transactional
    public EventCreateResult createUserEvent(UserEventCreateRequest dto, Long userId) {
        //겹침 체크 (hasOverlap)
        boolean hasOverlap = eventRepository.existsOverlap(
                userId, dto.getStartDatetime(), dto.getEndDatetime());

        //Entity 변환 및 연관관계 처리
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
        event.setCreatedBy(user);
        event.setUpdatedBy(user);
        event.setCreatedAt(LocalDateTime.now());
        event.setUpdatedAt(LocalDateTime.now());

        // 참여자 연관관계 (본인 자동 추가)
        EventParticipant self = new EventParticipant();
        self.setEvent(event);
        self.setUser(user);
        self.setStatus(ParticipantStatus.ACCEPTED);
        //event.getParticipants().add(self);

        // 추가 참여자 처리
        if (dto.getParticipantIds() != null) {
            for (Long pid : dto.getParticipantIds()) {
                if(pid.equals(user.getId())) continue; // 중복 방지
                User participant = userRepository.findById(pid)
                        .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));
                EventParticipant ep = new EventParticipant();
                ep.setEvent(event);
                ep.setUser(participant);
                ep.setStatus(ParticipantStatus.ACCEPTED);
                //event.getParticipants().add(ep);
            }
        }
        eventRepository.save(event);

        // 결과 리턴 (겹침 여부 함께)
        return new EventCreateResult(event.getId(), hasOverlap);
    }
}


