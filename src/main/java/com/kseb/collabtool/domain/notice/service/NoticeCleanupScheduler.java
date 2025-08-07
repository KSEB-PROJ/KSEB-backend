package com.kseb.collabtool.domain.notice.service;

import com.kseb.collabtool.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NoticeCleanupScheduler {

    private final NoticeRepository noticeRepository;

    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupExpiredNotices() {
        noticeRepository.deleteExpiredNotices(LocalDateTime.now());
    }
}