package com.kseb.collabtool.domain.message.repository;

import com.kseb.collabtool.domain.message.entity.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageTypeRepository extends JpaRepository<MessageType, Short> {
    // 메시지 타입에 따른 엔티티 조회
    Optional<MessageType> findByCode(String code);
}