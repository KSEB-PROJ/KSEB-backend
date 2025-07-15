package com.kseb.collabtool.domain.notice.service;

import com.kseb.collabtool.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NoticeCleanupScheduler {
    private final NoticeRepository noticeRepository;

    // 매 시간마다 실행 (cron: 초 분 시 일 월 요일)
    @Scheduled(cron = "0 0 * * * *") // 매 정각마다
    @Transactional
    public void deleteExpiredNotices() {
        noticeRepository.deleteExpiredNotices();
    }
}
