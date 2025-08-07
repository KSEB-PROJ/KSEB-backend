package com.kseb.collabtool.domain.admin.service;

import com.kseb.collabtool.domain.admin.dto.DashboardResponse;
import com.kseb.collabtool.domain.admin.dto.GroupAdminResponse;
import com.kseb.collabtool.domain.admin.dto.LogResponse;
import com.kseb.collabtool.domain.admin.dto.UserAdminResponse;
import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.repository.GroupRepository;
import com.kseb.collabtool.domain.log.entity.ActionType;
import com.kseb.collabtool.domain.log.entity.ActivityLog;
import com.kseb.collabtool.domain.log.repository.ActivityLogRepository;
import com.kseb.collabtool.domain.user.entity.Role;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final ActivityLogRepository activityLogRepository;

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

    public Page<UserAdminResponse> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserAdminResponse::from);
    }

    @Transactional
    public void updateUserRole(Long userId, Role newRole, User adminUser) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userToUpdate.setRole(newRole);
        userRepository.save(userToUpdate);

        // Log activity
        activityLogRepository.save(ActivityLog.builder()
                .actor(adminUser)
                .actionType(ActionType.ADMIN_CHANGE_USER_ROLE)
                .targetId(userId)
                .details("Role changed to " + newRole)
                .build());
    }

    @Transactional
    public void deleteUser(Long userId, User adminUser) {
        userRepository.deleteById(userId);

        // Log activity
        activityLogRepository.save(ActivityLog.builder()
                .actor(adminUser)
                .actionType(ActionType.ADMIN_DELETE_USER)
                .targetId(userId)
                .build());
    }

    public Page<GroupAdminResponse> getGroups(Pageable pageable) {
        return groupRepository.findAll(pageable).map(GroupAdminResponse::from);
    }

    @Transactional
    public void deleteGroup(Long groupId, User adminUser) {
        groupRepository.deleteById(groupId);

        // Log activity
        activityLogRepository.save(ActivityLog.builder()
                .actor(adminUser)
                .actionType(ActionType.ADMIN_DELETE_GROUP)
                .targetId(groupId)
                .build());
    }

    public Page<LogResponse> getLogs(Pageable pageable) {
        return activityLogRepository.findAll(pageable).map(LogResponse::from);
    }
}
