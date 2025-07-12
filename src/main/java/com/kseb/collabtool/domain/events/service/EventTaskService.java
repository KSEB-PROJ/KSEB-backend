package com.kseb.collabtool.domain.events.service;

import com.kseb.collabtool.domain.events.dto.EventTaskCreateRequest;
import com.kseb.collabtool.domain.events.dto.EventTaskResponse;
import com.kseb.collabtool.domain.events.dto.MyTaskResponse;
import com.kseb.collabtool.domain.events.entity.Event;
import com.kseb.collabtool.domain.events.entity.EventTask;
import com.kseb.collabtool.domain.events.entity.OwnerType;
import com.kseb.collabtool.domain.events.entity.TaskStatus;
import com.kseb.collabtool.domain.events.repository.EventRepository;
import com.kseb.collabtool.domain.events.repository.EventTaskRepository;
import com.kseb.collabtool.domain.events.repository.TaskStatusRepository;
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

@Service
@RequiredArgsConstructor
public class EventTaskService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventTaskRepository eventTaskRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public EventTaskResponse addTaskToEvent(Long eventId, EventTaskCreateRequest dto, Long currentUserId) {
        // 이벤트 조회 및 예외 처리
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(Status.EVENT_NOT_FOUND));

        OwnerType ownerType = event.getOwnerType();
        Long ownerId = event.getOwnerId();

        //소유자 유형별 권한 체크
        if (ownerType == OwnerType.USER) {
            // 개인 일정은 본인만 추가 가능
            if (!ownerId.equals(currentUserId)) {
                throw new GeneralException(Status.NO_AUTHORITY);
            }
        } else if (ownerType == OwnerType.GROUP) {
            // 그룹 일정은 그룹 멤버만 추가 가능
            if (!groupMemberRepository.existsByGroupIdAndUserId(ownerId, currentUserId)) {
                throw new GeneralException(Status.NO_AUTHORITY);
            }
        } else {
            throw new GeneralException(Status.BAD_REQUEST); // 둘다 없으면 예외
        }

        // 담당자(assignee) 지정: null이면 본인, 그룹 일정이면 그룹 멤버인지 추가 체크
        Long assigneeId = dto.getAssigneeId() != null ? dto.getAssigneeId() : currentUserId;
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));

        if (ownerType == OwnerType.GROUP) {
            // 담당자도 그룹 멤버여야 함
            if (!groupMemberRepository.existsByGroupIdAndUserId(ownerId, assigneeId)) {
                throw new GeneralException(Status.NO_AUTHORITY);
            }
        }

        // 할 일 상태(TaskStatus) 조회 및 검증
        TaskStatus status = taskStatusRepository.findById(dto.getStatusId().shortValue())
                .orElseThrow(() -> new GeneralException(Status.BAD_REQUEST));

        // EventTask 생성 및 저장
        EventTask task = new EventTask();
        task.setEvent(event);
        task.setTitle(dto.getTitle());
        task.setAssignee(assignee);
        task.setTaskStatus(status);
        task.setDueDatetime(dto.getDueDatetime());
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        eventTaskRepository.save(task);

        return new EventTaskResponse(task);
    }

    @Transactional
    public List<EventTaskResponse> getTasksByEvent(Long eventId, Long currentUserId) {
        // 이벤트 조회
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(Status.EVENT_NOT_FOUND));

        OwnerType ownerType = event.getOwnerType();
        Long ownerId = event.getOwnerId();

        //개인은 본인만, 그룹은 멤버만 조회 가능
        if (ownerType == OwnerType.USER) {
            if (!ownerId.equals(currentUserId)) {
                throw new GeneralException(Status.NO_AUTHORITY);
            }
        } else if (ownerType == OwnerType.GROUP) {
            if (!groupMemberRepository.existsByGroupIdAndUserId(ownerId, currentUserId)) {
                throw new GeneralException(Status.NO_AUTHORITY);
            }
        } else {
            throw new GeneralException(Status.BAD_REQUEST);
        }

        // 해당 이벤트의 할 일 목록 조회
        List<EventTask> tasks = eventTaskRepository.findByEvent_Id(eventId);

        // DTO 변환해서 던져줌
        return tasks.stream()
                .map(EventTaskResponse::new)
                .toList();
    }


    @Transactional
    public List<MyTaskResponse> getTasksByAssignee(Long assigneeId, Long currentUserId) { //이벤트랑 해당 이벤트에서 해야되는 task 반환
        if (!assigneeId.equals(currentUserId)) { //현재 유저랑 담당자 같은지 확인
            throw new GeneralException(Status.NO_AUTHORITY);
        }
        List<EventTask> tasks = eventTaskRepository.findByAssignee_Id(assigneeId);
        return tasks.stream().map(MyTaskResponse::new).toList();
    }

}
