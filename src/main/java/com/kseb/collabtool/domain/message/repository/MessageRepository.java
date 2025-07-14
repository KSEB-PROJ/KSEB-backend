package com.kseb.collabtool.domain.message.repository;

import com.kseb.collabtool.domain.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChannelIdAndDeletedFalseOrderByCreatedAtAsc(Long channelId);
}