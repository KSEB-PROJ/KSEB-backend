package com.kseb.collabtool.domain.notice.repository;

import com.kseb.collabtool.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByGroup_IdOrderByPinnedUntilDescCreatedAtDesc(Long groupId);
}
