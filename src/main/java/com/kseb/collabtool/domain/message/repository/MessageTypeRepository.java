package com.kseb.collabtool.domain.message.repository;

import com.kseb.collabtool.domain.message.entity.MessageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MessageTypeRepository extends JpaRepository<MessageType, Short> {
    Optional<MessageType> findByCode(String code);
}