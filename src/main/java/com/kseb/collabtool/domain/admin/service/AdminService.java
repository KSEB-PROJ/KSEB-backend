package com.kseb.collabtool.domain.admin.service;

import com.kseb.collabtool.domain.admin.dto.*;
import com.kseb.collabtool.domain.channel.repository.ChannelRepository;
import com.kseb.collabtool.domain.events.repository.EventRepository;
import com.kseb.collabtool.domain.events.repository.EventTaskRepository;
import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.repository.GroupMemberRepository;
import com.kseb.collabtool.domain.groups.repository.GroupRepository;
import com.kseb.collabtool.domain.log.entity.ActionType;
import com.kseb.collabtool.domain.log.entity.ActivityLog;
import com.kseb.collabtool.domain.log.repository.ActivityLogRepository;
import com.kseb.collabtool.domain.log.repository.ActivityLogSpecification;
import com.kseb.collabtool.domain.log.service.ActivityLogService;
import com.kseb.collabtool.domain.message.repository.MessageRepository;
import com.kseb.collabtool.domain.notice.repository.NoticeRepository;
import com.kseb.collabtool.domain.user.entity.Role;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ActivityLogRepository activityLogRepository;
    private final ActivityLogService activityLogService;
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final NoticeRepository noticeRepository;
    private final EventRepository eventRepository;
    private final EventTaskRepository eventTaskRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        long totalUsers = userRepository.count();
        long todayRegisteredUsers = userRepository.countByCreatedAtAfter(LocalDate.now().atStartOfDay());
        long totalGroups = groupRepository.count();

        return DashboardResponse.builder()
                .totalUsers(totalUsers)
                .todayRegisteredUsers(todayRegisteredUsers)
                .totalGroups(totalGroups)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<UserAdminResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserAdminResponse::from);
    }

    public void updateUserRole(Long userId, Role newRole, User adminUser) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userToUpdate.setRole(newRole);
        // @Transactional에 의해 더티 체킹으로 자동 저장됨
        activityLogService.saveLog(adminUser, ActionType.ADMIN_CHANGE_USER_ROLE, userId, "Role changed to " + newRole);
    }

    public void deleteUser(Long userId, User adminUser) {
        // --- 사용자 삭제 전, 모든 외래 키 제약 조건 해결 ---

        // 1. EventTask: 담당자(assignee) 연결 끊기
        eventTaskRepository.unassignUserFromTasks(userId);

        // 2. ActivityLog: 작성자(actor) 연결 끊기
        activityLogRepository.nullifyActorForUser(userId);

        // 3. Notice: 작성한 공지 모두 삭제
        noticeRepository.deleteByUserId(userId);

        // 4. GroupMember: 사용자가 속한 모든 그룹에서 탈퇴 처리
        groupMemberRepository.deleteByUserId(userId);

        // 5. Group: 사용자가 생성한 그룹을 '개별적으로' 삭제하여 CascadeType.ALL 트리거
        List<Group> ownedGroups = groupRepository.findByOwnerId(userId);
        groupRepository.deleteAll(ownedGroups); // deleteAll은 개별 delete를 호출하여 cascade를 보장
        
        // --- 모든 연결이 끊어진 후, 사용자 삭제 ---
        userRepository.deleteById(userId);
        
        // --- 최종 로그 기록 ---
        activityLogService.saveLog(adminUser, ActionType.ADMIN_DELETE_USER, userId);
    }

    @Transactional(readOnly = true)
    public Page<GroupAdminResponse> getGroups(Pageable pageable) {
        return groupRepository.findAll(pageable).map(GroupAdminResponse::from);
    }

    public void deleteGroup(Long groupId, User adminUser) {
        groupRepository.deleteById(groupId);
        activityLogService.saveLog(adminUser, ActionType.ADMIN_DELETE_GROUP, groupId);
    }

    @Transactional(readOnly = true)
    public Page<LogResponse> getLogs(Pageable pageable, String actorName, List<ActionType> actionTypes, LocalDate startDate, LocalDate endDate) {
        Specification<ActivityLog> spec = ActivityLogSpecification.withFilter(actorName, actionTypes, startDate, endDate);
        return activityLogRepository.findAll(spec, pageable).map(LogResponse::from);
    }

    @Transactional(readOnly = true)
    public List<DailyRegistrationDTO> getDailyRegistrations(int days) {
        LocalDateTime startDate = LocalDate.now().minusDays(days - 1).atStartOfDay();
        List<Map<String, Object>> results = userRepository.findDailyRegistrationsSince(startDate);
        return results.stream()
                .map(result -> new DailyRegistrationDTO(
                        ((Date) result.get("date")).toLocalDate(),
                        ((Number) result.get("count")).longValue()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HourlyActivityDTO> getHourlyActivity(int hours) {
        LocalDateTime startDate = LocalDateTime.now().minusHours(hours);
        List<Map<String, Object>> results = messageRepository.findHourlyActivitySince(startDate);
        return results.stream()
                .map(result -> new HourlyActivityDTO(
                        (Integer) result.get("hour"),
                        ((Number) result.get("count")).longValue()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ContentTypeDistributionDTO getContentTypeDistribution() {
        return ContentTypeDistributionDTO.builder()
                .groupCount(groupRepository.count())
                .channelCount(channelRepository.count())
                .noticeCount(noticeRepository.count())
                .eventCount(eventRepository.count())
                .build();
    }
}