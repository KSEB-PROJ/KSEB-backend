package com.kseb.collabtool.domain.notice.repository;

import com.kseb.collabtool.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    // 특정 그룹의 공지들을 고정 만료일 내림차순, 생성일 내림차순으로 정렬해서 조회
    List<Notice> findByGroup_IdOrderByPinnedUntilDescCreatedAtDesc(Long groupId);

    // 만료된 공지 삭제 쿼리 (pinnedUntil이 현재시각보다 이전인 경우)
    @Modifying
    @Transactional
    @Query("DELETE FROM Notice n WHERE n.pinnedUntil IS NOT NULL AND n.pinnedUntil < CURRENT_TIMESTAMP")
    void deleteExpiredNotices();
}
