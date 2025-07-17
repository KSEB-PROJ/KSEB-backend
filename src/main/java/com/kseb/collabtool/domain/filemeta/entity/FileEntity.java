package com.kseb.collabtool.domain.filemeta.entity;
import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
@Getter @Setter
@Entity
@Table(name = "files")
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 파일 PK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    private Channel channel; // 소속 채널

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 업로더

    @Column(name = "file_url", nullable = false, length = 512)
    private String fileUrl; // 저장 경로(URI/URL)

    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName; // 원본 파일명

    @Column(name = "file_type", length = 20)
    private String fileType; // 확장자/키워드 (ex: pdf, hwp, jpg)

    @Column(name = "file_size")
    private Long fileSize; // 바이트 단위 크기

    @Column(name = "mime_type", length = 100)
    private String mimeType; // MIME 타입 (image/png, application/pdf 등)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 업로드 일시

}
