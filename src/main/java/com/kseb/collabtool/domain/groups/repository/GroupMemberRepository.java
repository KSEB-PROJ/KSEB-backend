package com.kseb.collabtool.domain.groups.repository;

import com.kseb.collabtool.domain.groups.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupMemberRepository extends JpaRepository<GroupMember,Long> {
}
