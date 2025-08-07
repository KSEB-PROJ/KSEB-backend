package com.kseb.collabtool.domain.groups.repository;

import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.groups.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);
    List<GroupMember> findByUserId(Long userId);
    List<GroupMember> findByGroupId(Long groupId);

    @Query("SELECT gm.group FROM GroupMember gm WHERE gm.user.id = :userId")
    List<Group> findGroupsByUserId(@Param("userId") Long userId);

    @Query("SELECT gm.user.id FROM GroupMember gm WHERE gm.group.id = :groupId")
    List<Long> findUserIdsByGroupId(@Param("groupId") Long groupId);

    @Modifying
    @Query("DELETE FROM GroupMember gm WHERE gm.user.id = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}