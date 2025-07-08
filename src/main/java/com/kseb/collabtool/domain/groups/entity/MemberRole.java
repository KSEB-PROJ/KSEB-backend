package com.kseb.collabtool.domain.groups.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "member_roles")
@Data
public class MemberRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id; // TINYINT PK

    @Column(nullable = false, unique = true, length = 32)
    private String code; // LEADER, MEMBER 등   LEADER랑 MEMBER를 넣어둬야함.

    @Column(nullable = false, length = 64)
    private String name; // 표시명 (리더, 멤버)

}

