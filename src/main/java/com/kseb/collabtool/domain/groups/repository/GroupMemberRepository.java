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
            "g.id, g.name, g.code, nc.id) " +
            "FROM GroupMember m " +
            "JOIN m.group g " +
            "LEFT JOIN g.noticeChannel nc " +
            "WHERE m.user.id = :userId " +
            "GROUP BY g.id, g.name, g.code, nc.id " +
            "ORDER BY g.id DESC")

    List<GroupListDto> findGroupsByUserId(@Param("userId") Long userId); //내가 속한 그룹들

    List<GroupMember> findByGroup_Id(Long groupId);

    boolean existsByGroupIdAndUserId(Long groupId, Long userId);

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

    @Query("SELECT gm.user.id FROM GroupMember gm WHERE gm.group.id = :groupId")
    List<Long> findUserIdsByGroupId(@Param("groupId") Long groupId); //특정 그룹에 속한 모든 멤버의 id 리스트
}
