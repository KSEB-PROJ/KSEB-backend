package com.kseb.collabtool.domain.message.service;

import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.message.dto.ChatRequest;
import com.kseb.collabtool.domain.message.dto.ChatResponse;

import com.kseb.collabtool.domain.channel.repository.ChannelRepository;
import com.kseb.collabtool.domain.message.entity.Message;
import com.kseb.collabtool.domain.message.entity.MessageType;
import com.kseb.collabtool.domain.message.repository.MessageRepository;
import com.kseb.collabtool.domain.message.repository.MessageTypeRepository;
import com.kseb.collabtool.domain.notice.entity.Notice;
import com.kseb.collabtool.domain.notice.repository.NoticeRepository;
import com.kseb.collabtool.domain.user.entity.User;
import com.kseb.collabtool.domain.user.repository.UserRepository;
import com.kseb.collabtool.global.exception.GeneralException;
import com.kseb.collabtool.global.exception.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final NoticeRepository noticeRepository;
    private final MessageRepository messageRepository;
    private final MessageTypeRepository messageTypeRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Transactional
    public ChatResponse sendMessage(Long userId, ChatRequest request) {
        Channel channel = channelRepository.findById(request.getChannelId())
                .orElseThrow(() -> new IllegalArgumentException("채널이 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다."));
        MessageType messageType = messageTypeRepository.findById(request.getMessageTypeId())
                .orElseThrow(() -> new IllegalArgumentException("메시지 타입이 존재하지 않습니다."));

        Message message = new Message();
        message.setChannel(channel);
        message.setUser(user);
        message.setContent(request.getContent());
        message.setMessageType(messageType);
        message.setFileUrl(request.getFileUrl());
        message.setFileName(request.getFileName());

        Message saved = messageRepository.save(message);

        return toResponse(saved, userId);
    }

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
                .orElseThrow(() -> new IllegalArgumentException("메시지가 존재하지 않습니다."));
        if (!message.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 메시지만 수정할 수 있습니다.");
        }
        if (request.getContent() != null) message.setContent(request.getContent());
        if (request.getFileUrl() != null) message.setFileUrl(request.getFileUrl());
        if (request.getFileName() != null) message.setFileName(request.getFileName());
        // 메시지 타입 변경은 필요시만 허용
        if (request.getMessageTypeId() != null) {
            MessageType messageType = messageTypeRepository.findById(request.getMessageTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("메시지 타입이 존재하지 않습니다."));
            message.setMessageType(messageType);
        }
        return toResponse(message, userId);
    }

    @Transactional
    public void deleteMessage(Long userId, Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지가 존재하지 않습니다."));
        if (!message.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 메시지만 삭제할 수 있습니다.");
        }
        message.setDeleted(true);
    }

    private ChatResponse toResponse(Message message, Long currentUserId) {
        return ChatResponse.builder()
                .id(message.getId())
                .channelId(message.getChannel().getId())
                .userId(message.getUser().getId())
                .userName(message.getUser().getName()) // User 엔티티에 getName()이 있다고 가정
                .content(message.getContent())
                .messageType(message.getMessageType().getCode())
                .fileUrl(message.getFileUrl())
                .fileName(message.getFileName())
                .isMine(message.getUser().getId().equals(currentUserId))
                .createdAt(message.getCreatedAt())
                .build();
    }
    public Notice promoteMessageToNotice(Long channelId, Long messageId, Long userId) {
        // 메시지 조회
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new GeneralException(Status.NOT_FOUND));

        // 채널 체크
        if (!message.getChannel().getId().equals(channelId)) {
            throw new GeneralException(Status.BAD_REQUEST); // 필요시 별도의 Status 추가 가능
        }

        // **작성자(로그인 유저)와 메시지 작성자 일치 체크**
        if (!message.getUser().getId().equals(userId)) {
            throw new GeneralException(Status.NOTICE_PROMOTE_ONLY_SELF); // Status에 추가한 항목 사용
        }

        // 공지 생성 (Notice)
        Notice notice = new Notice();
        notice.setGroup(message.getChannel().getGroup());
        notice.setChannel(message.getChannel());
        notice.setUser(message.getUser());
        notice.setSourceMessage(message);
        notice.setContent(message.getContent());
        notice.setCreatedAt(java.time.LocalDateTime.now());

        return noticeRepository.save(notice);
    }

}