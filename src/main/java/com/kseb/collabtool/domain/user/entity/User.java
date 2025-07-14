package com.kseb.collabtool.domain.user.entity;

import jakarta.persistence.*;
import jdk.jshell.Snippet;
import lombok.*;

import java.time.LocalDateTime;
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password; // BCrypt 해시 저장

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "profile_img", length = 512)
    private String profileImg; // NULL 허용

    @Column(name = "created_at", nullable = false , columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

}
