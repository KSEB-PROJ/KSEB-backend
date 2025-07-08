package com.kseb.collabtool.domain.groups.repository;

import com.kseb.collabtool.domain.groups.dto.GroupListDto;
import com.kseb.collabtool.domain.groups.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember,Long> {
    @Query("SELECT new com.kseb.collabtool.domain.groups.dto.GroupListDto(" +
            "g.id, g.name, g.code, nc.id, COUNT(m)) " +
            "FROM GroupMember m " +
            "JOIN m.group g " +
            "LEFT JOIN g.noticeChannel nc " +
            "WHERE m.user.id = :userId " +
            "GROUP BY g.id, g.name, g.code, nc.id " +
            "ORDER BY g.id DESC")

    List<GroupListDto> findGroupsByUserId(@Param("userId") Long userId);

    List<GroupMember> findByGroup_Id(Long groupId);

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
}
