package com.kseb.collabtool.domain.events.service;

import com.kseb.collabtool.domain.events.dto.EventTaskCreateRequest;
import com.kseb.collabtool.domain.events.dto.EventTaskResponse;
import com.kseb.collabtool.domain.events.dto.TaskResponse;
import com.kseb.collabtool.domain.events.dto.UpdateTaskRequest;
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

    private void checkEventAuthority(Event event, Long userId) {
        boolean isParticipant = event.getParticipants().stream()
                .anyMatch(p -> p.getUser().getId().equals(userId));
        if (!isParticipant) {
            throw new GeneralException(Status.NO_AUTHORITY);
        }
    }

    @Transactional
    public EventTaskResponse addTaskToEvent(Long eventId, EventTaskCreateRequest dto, Long currentUserId) {
        // 이벤트 조회 및 예외 처리
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new GeneralException(Status.EVENT_NOT_FOUND));

        // 참여자인지 확인하는 방식으로 권한 체크 통일
        checkEventAuthority(event, currentUserId);

        // 담당자(assignee) 지정: null이면 본인, 그룹 일정이면 그룹 멤버인지 추가 체크
        Long assigneeId = dto.getAssigneeId() != null ? dto.getAssigneeId() : currentUserId;
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));

        if (event.getOwnerType() == OwnerType.GROUP) {
            // 담당자도 그룹 멤버여야 함
            if (!groupMemberRepository.existsByGroupIdAndUserId(event.getOwnerId(), assigneeId)) {
                throw new GeneralException(Status.NO_AUTHORITY, "담당자는 그룹 멤버여야 합니다.");
            }
        }

        // 할 일 상태(TaskStatus) 조회 및 검증
        TaskStatus status = taskStatusRepository.findById(dto.getStatusId().shortValue())
                .orElseThrow(() -> new GeneralException(Status.BAD_REQUEST, "잘못된 상태 ID입니다."));

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

        // 참여자인지 확인하는 방식으로 권한 체크 통일
        checkEventAuthority(event, currentUserId);

        // 해당 이벤트의 할 일 목록 조회
        List<EventTask> tasks = eventTaskRepository.findByEvent_Id(eventId);

        // DTO 변환해서 던져줌
        return tasks.stream()
                .map(EventTaskResponse::new)
                .toList();
    }


    @Transactional
    public List<TaskResponse> getTasksByAssignee(Long assigneeId, Long currentUserId) { //이벤트랑 해당 이벤트에서 해야되는 task 반환
        if (!assigneeId.equals(currentUserId)) { //현재 유저랑 담당자 같은지 확인
            throw new GeneralException(Status.NO_AUTHORITY);
        }
        List<EventTask> tasks = eventTaskRepository.findByAssignee_Id(assigneeId);
        return tasks.stream().map(TaskResponse::new).toList();
    }

    @Transactional
    public EventTaskResponse updateTask(Long taskId, UpdateTaskRequest req, Long currentUserId) {
        EventTask task = eventTaskRepository.findById(taskId)
                .orElseThrow(() -> new GeneralException(Status.TASK_NOT_FOUND));

        // 참여자인지 확인하는 방식으로 권한 체크 통일
        checkEventAuthority(task.getEvent(), currentUserId);

        // patch로 프론트에서 보낸 값만 바꿔줌
        if (req.getTitle() != null) {
            task.setTitle(req.getTitle());
        }
        if (req.getAssigneeId() != null) {
            // 담당자 변경: 그룹 일정이면 해당 그룹 멤버만 허용
            User assignee = userRepository.findById(req.getAssigneeId())
                    .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));
            if (task.getEvent().getOwnerType() == OwnerType.GROUP && !groupMemberRepository.existsByGroupIdAndUserId(task.getEvent().getOwnerId(), req.getAssigneeId())) {
                throw new GeneralException(Status.NO_AUTHORITY, "담당자는 그룹 멤버여야 합니다.");
            }
            task.setAssignee(assignee);
        }
        if (req.getStatusId() != null) {
            TaskStatus status = taskStatusRepository.findById(req.getStatusId())
                    .orElseThrow(() -> new GeneralException(Status.BAD_REQUEST, "잘못된 상태 ID입니다."));
            task.setTaskStatus(status);
        }
        if (req.getDueDatetime() != null) {
            task.setDueDatetime(req.getDueDatetime());
        }
        task.setUpdatedAt(LocalDateTime.now());

        return new EventTaskResponse(task);
    }


    @Transactional
    public void deleteTask(Long taskId, Long currentUserId) {
        EventTask task = eventTaskRepository.findById(taskId)
                .orElseThrow(() -> new GeneralException(Status.TASK_NOT_FOUND));

        // 참여자인지 확인하는 방식으로 권한 체크 통일
        checkEventAuthority(task.getEvent(), currentUserId);

        eventTaskRepository.delete(task);
    }



    @Transactional
    public List<TaskResponse> getTasksByGroup(Long groupId, Long currentUserId) {
        //그룹 멤버 권한 체크
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new GeneralException(Status.NO_AUTHORITY);
        }
        //해당 그룹 일정에 속한 모든 Task 조회
        List<EventTask> tasks = eventTaskRepository.findByGroupId(OwnerType.GROUP, groupId);

        //DTO 변환 (이벤트 정보 포함)
        return tasks.stream().map(TaskResponse::new).toList();
    }
}