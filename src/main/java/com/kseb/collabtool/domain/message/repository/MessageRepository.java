package com.kseb.collabtool.domain.message.repository;

import com.kseb.collabtool.domain.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message,Long> {
}
