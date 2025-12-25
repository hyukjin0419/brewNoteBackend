package com.pard.server.brewnotebackend.global.bootstrap;

import com.pard.server.brewnotebackend.domain.member.Member;
import com.pard.server.brewnotebackend.domain.member.MemberRepository;
import com.pard.server.brewnotebackend.domain.member.MemberRoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminBootstrapServiceImpl implements AdminBootstrapService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void initAdminIfNotExists(String adminEmail, String adminPassword) {

        if(memberRepository.existsByRole(MemberRoleType.ADMIN)) return;

        Member admin = Member.of(
                adminEmail,
                passwordEncoder.encode(adminPassword),
                "Admin",
                "admin",
                MemberRoleType.ADMIN
        );

        memberRepository.save(admin);
    }
}
