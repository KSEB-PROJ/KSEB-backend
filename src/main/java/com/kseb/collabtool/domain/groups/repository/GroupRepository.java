package com.kseb.collabtool.domain.groups.repository;

import com.kseb.collabtool.domain.groups.entity.Group;
import com.kseb.collabtool.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group,Long> {
    Optional<Group> findByCode(String code); // 초대코드로 그룹 찾기
    List<Group> findByOwner(User owner);     // 내 소유 그룹 찾기
}
