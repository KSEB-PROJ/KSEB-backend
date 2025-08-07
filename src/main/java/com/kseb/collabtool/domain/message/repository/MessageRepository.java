package com.kseb.collabtool.domain.message.repository;

import com.kseb.collabtool.domain.message.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface MessageRepository extends JpaRepository<Message, Long> {
    // 생성일 기준으로 삭제되지 않은 메시지를 정렬해서 반환
    List<Message> findByChannelIdAndDeletedFalseOrderByCreatedAtAsc(Long channelId);

    // 생성일 기준으로 모든 메시지(삭제 포함)를 정렬해서 반환
    List<Message> findByChannelIdOrderByCreatedAtAsc(Long channelId);

    @Query(value = "SELECT HOUR(created_at) as hour, COUNT(*) as count " +
                   "FROM messages " + // 'message' -> 'messages'로 수정
                   "WHERE created_at >= :startDate " +
                   "GROUP BY HOUR(created_at) " +
                   "ORDER BY HOUR(created_at)", nativeQuery = true)
    List<Map<String, Object>> findHourlyActivitySince(@Param("startDate") LocalDateTime startDate);
}
