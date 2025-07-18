package com.kseb.collabtool.domain.message.service;

import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.filemeta.entity.FileEntity;
import com.kseb.collabtool.domain.filemeta.repository.FileRepository;
import com.kseb.collabtool.domain.message.dto.ChatRequest;
import com.kseb.collabtool.domain.message.dto.ChatResponse;
import com.kseb.collabtool.domain.channel.repository.ChannelRepository;
import com.kseb.collabtool.domain.message.entity.Message;
import com.kseb.collabtool.domain.message.entity.MessageType;
import com.kseb.collabtool.domain.message.repository.MessageRepository;
import com.kseb.collabtool.domain.message.repository.MessageTypeRepository;
import com.kseb.collabtool.domain.notice.dto.NoticeResponse;
import com.kseb.collabtool.domain.notice.entity.Notice;
import com.kseb.collabtool.domain.notice.repository.NoticeRepository;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.repository.UserRepository;
import com.kseb.collabtool.global.exception.GeneralException;
import com.kseb.collabtool.global.exception.Status;
import com.kseb.collabtool.domain.filemeta.service.FileUtil; // 유틸 import
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final NoticeRepository noticeRepository;
    private final MessageRepository messageRepository;
    private final MessageTypeRepository messageTypeRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @Transactional
    public ChatResponse sendMessage(Long userId, ChatRequest request, MultipartFile file) {
        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new GeneralException(Status.CHANNEL_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new GeneralException(Status.USER_NOT_FOUND));
        MessageType messageType = messageTypeRepository.findById(request.getMessageTypeId())
                .orElseThrow(() -> new GeneralException(Status.BAD_REQUEST));

        Message message = new Message();
        message.setChannel(channel);
        message.setUser(user);
        message.setContent(request.getContent());
        message.setMessageType(messageType);

        // 파일 처리
        if (file != null && !file.isEmpty()) {
            try {
                String fileUrl = FileUtil.saveFile(file);

                // ===[ 파일 메타 DB 저장! ]===
                FileEntity fileEntity = new FileEntity();
                fileEntity.setChannel(channel);
                fileEntity.setUser(user);
                fileEntity.setFileUrl(fileUrl);
                fileEntity.setFileName(file.getOriginalFilename());
                fileEntity.setFileType(getFileExt(file.getOriginalFilename()));
                fileEntity.setMimeType(file.getContentType());
                fileEntity.setFileSize(file.getSize());
                fileRepository.save(fileEntity);
                // =========================

                // 메시지 테이블에도 파일 정보 저장
                message.setFileUrl(fileUrl);
                message.setFileName(file.getOriginalFilename());
            } catch (Exception e) {
                throw new GeneralException(Status.INTERNAL_SERVER_ERROR, "파일 저장 실패");
            }
        } else {
            message.setFileUrl(null);
            message.setFileName(null);
        }

        Message saved = messageRepository.save(message);

        return toResponse(saved, userId);
    }

    // 파일 확장자 추출 함수
    private String getFileExt(String fileName) {
        if (fileName == null || !fileName.contains(".")) return "";
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    // 나머지 메서드는 기존 그대로 유지

    @Transactional(readOnly = true)
    public List<ChatResponse> getMessages(Long channelId, Long currentUserId) {
        List<Message> messages = messageRepository.findByChannelIdAndDeletedFalseOrderByCreatedAtAsc(channelId);
        return messages.stream()
                .map(m -> toResponse(m, currentUserId))
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatResponse updateMessage(Long userId, Long messageId, ChatRequest request) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(Status.NOT_FOUND));
        if (!message.getUser().getId().equals(userId)) {
            throw new GeneralException(Status.FORBIDDEN);
        }
        if (request.getContent() != null) message.setContent(request.getContent());
        if (request.getFileUrl() != null) message.setFileUrl(request.getFileUrl());
        if (request.getFileName() != null) message.setFileName(request.getFileName());
        if (request.getMessageTypeId() != null) {
            MessageType messageType = messageTypeRepository.findById(request.getMessageTypeId())
                    .orElseThrow(() -> new GeneralException(Status.BAD_REQUEST));
            message.setMessageType(messageType);
        }
        return toResponse(message, userId);
    }

    @Transactional
    public NoticeResponse promoteMessageToNotice(
            Long channelId, Long messageId, Long userId, LocalDateTime pinnedUntil
    ) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(Status.NOT_FOUND));
        if (!message.getChannel().getId().equals(channelId)) {
            throw new GeneralException(Status.BAD_REQUEST);
        }
        if (!message.getUser().getId().equals(userId)) {
            throw new GeneralException(Status.FORBIDDEN);
        }
        Notice notice = new Notice();
        notice.setGroup(message.getChannel().getGroup());
        notice.setChannel(message.getChannel());
        notice.setUser(message.getUser());
        notice.setSourceMessage(message);
        notice.setContent(message.getContent());
        notice.setPinnedUntil(pinnedUntil);
        notice.setCreatedAt(LocalDateTime.now());

        Notice saved = noticeRepository.save(notice);

        return NoticeResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(Status.NOT_FOUND));
        if (!message.getUser().getId().equals(userId)) {
            throw new GeneralException(Status.FORBIDDEN);
        }

        // 1. 파일 정보 삭제 (DB/폴더 동시)
        if (message.getFileUrl() != null) {
            // 1-1. 파일 DB에 저장된 fileUrl로 파일엔터티 조회 (예시)
            Optional<FileEntity> fileOpt = fileRepository.findByFileUrl(message.getFileUrl());
            fileOpt.ifPresent(file -> {
                // 실제 파일 삭제
                FileUtil.deleteFile(file.getFileUrl());
                // 파일 엔터티 삭제
                fileRepository.delete(file);
            });
        }

        // 2. 메시지 소프트 삭제 또는 완전 삭제
        messageRepository.delete(message);
    }

    private ChatResponse toResponse(Message message, Long currentUserId) {
        return ChatResponse.builder()
                .id(message.getId())
                .channelId(message.getChannel().getId())
                .userId(message.getUser().getId())
                .userName(message.getUser().getName())
                .content(message.getContent())
                .messageType(message.getMessageType().getCode())
                .fileUrl(message.getFileUrl())
                .fileName(message.getFileName())
                .isMine(message.getUser().getId().equals(currentUserId))
                .createdAt(message.getCreatedAt())
                .build();
    }
}
