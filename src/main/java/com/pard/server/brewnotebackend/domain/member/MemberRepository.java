package com.pard.server.brewnotebackend.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member,String> {
    boolean existsByRole(MemberRoleType roleType);
    boolean existsByEmail(String email);
}
