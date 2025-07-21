package com.kseb.collabtool.domain.groups.initializer;

import com.kseb.collabtool.domain.groups.entity.MemberRole;
import com.kseb.collabtool.domain.groups.repository.MemberRoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberRoleInitializer {
    //예외 커스텀, 트렉잭션 처리,
    private final MemberRoleRepository memberRoleRepository;

    @PostConstruct
    public void init() {
        // LEADER
        if (memberRoleRepository.findByCode("LEADER").isEmpty()) {
            MemberRole leader = new MemberRole();
            leader.setCode("LEADER");
            leader.setName("리더");
            memberRoleRepository.save(leader);
        }
        // MEMBER
        if (memberRoleRepository.findByCode("MEMBER").isEmpty()) {
            MemberRole member = new MemberRole();
            member.setCode("MEMBER");
            member.setName("멤버");
            memberRoleRepository.save(member);
        }
    }
}
