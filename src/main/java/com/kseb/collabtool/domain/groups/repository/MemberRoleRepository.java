package com.kseb.collabtool.domain.groups.repository;

import com.kseb.collabtool.domain.groups.entity.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRoleRepository extends JpaRepository<MemberRole,Short> {
    Optional<MemberRole> findByCode(String code); //역할코드
}
