package com.kseb.collabtool.domain.message.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "message_types")
@Data
public class MessageType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id; // TINYINT PK

    @Column(nullable = false, unique = true, length = 32)
    private String code; // TEXT, DOCUMENT, IMAGE, VIDEO 등

    @Column(nullable = false, length = 64)
    private String name; // 표시명 (텍스트, 문서, 이미지, 동영상)

}

/* 초기 셋팅값
INSERT INTO message_types (code, name)
VALUES
  ('TEXT', '텍스트'),
  ('DOCUMENT', '문서'),
  ('IMAGE', '이미지'),
  ('VIDEO', '동영상');
 */