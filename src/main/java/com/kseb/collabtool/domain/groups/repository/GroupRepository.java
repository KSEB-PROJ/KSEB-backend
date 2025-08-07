package com.kseb.collabtool.domain.groups.repository;

import com.kseb.collabtool.domain.groups.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByCode(String code);

    List<Group> findByOwnerId(Long ownerId); // 추가된 메소드

    @Modifying
    @Query("DELETE FROM Group g WHERE g.owner.id = :userId")
    void deleteByOwnerId(@Param("userId") Long userId);
}
