package com.kseb.collabtool.domain.notice.repository;

import com.kseb.collabtool.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByGroupIdOrderByCreatedAtDesc(Long groupId);

    @Modifying
    @Query("DELETE FROM Notice n WHERE n.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM Notice n WHERE n.pinnedUntil IS NOT NULL AND n.pinnedUntil < :now")
    void deleteExpiredNotices(@Param("now") LocalDateTime now);
}