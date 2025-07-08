package com.kseb.collabtool.domain.channel.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "channel_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id; // TINYINT와 매핑, PK

    @Column(nullable = false, unique = true, length = 32)
    private String code; // CHAT / CALENDAR / NOTICE 등 1 2 3

    @Column(nullable = false, length = 64)
    private String name; // 표시명(채팅 / 캘린더)

    @Column(length = 255)
    private String description; // 상세 설명 텍스트

}
