package com.kseb.collabtool.domain.groups.repository;

import com.kseb.collabtool.domain.groups.entity.MemberRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRole extends JpaRepository<MemberRole,Long> {
    
}
