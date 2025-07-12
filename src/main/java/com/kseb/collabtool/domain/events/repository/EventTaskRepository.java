package com.kseb.collabtool.domain.events.repository;


import com.kseb.collabtool.domain.events.entity.EventTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventTaskRepository extends JpaRepository<EventTask, Long> {
}
