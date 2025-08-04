package com.kseb.collabtool.domain.message.repository;

import com.kseb.collabtool.domain.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 생성일 기준으로 삭제되지 않은 메시지를 정렬해서 반환
    List<Message> findByChannelIdAndDeletedFalseOrderByCreatedAtAsc(Long channelId);

    // 생성일 기준으로 모든 메시지(삭제 포함)를 정렬해서 반환
    List<Message> findByChannelIdOrderByCreatedAtAsc(Long channelId);

}