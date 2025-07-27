package com.kseb.collabtool.domain.events.repository;

import com.kseb.collabtool.domain.events.entity.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Short> {
}
