package com.kseb.collabtool.domain.groups.entity;

import com.kseb.collabtool.domain.channel.entity.Channel;
import com.kseb.collabtool.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "study_groups", uniqueConstraints = {
        @UniqueConstraint(columnNames = "code")
})
@Data
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // 그룹명

    @Column(nullable = false, length = 50, unique = true)
    private String code; // 초대 코드/URL

    @ManyToOne(fetch = FetchType.LAZY) //하나의 그룹(Group)은 하나의 생성자(User)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner; // 그룹 생성자(리더), FK(users.id)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_channel_id", unique = true)
    private Channel noticeChannel; // 공지 전용 NOTICE 채널, FK(channels.id), NULL 허용

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    //그룹 삭제 시 group_members, channels 등 연관 엔티티까지 같이 삭제
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<GroupMember> groupMembers;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true)
    private java.util.List<Channel> channels;
}

