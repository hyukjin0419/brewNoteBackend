package com.pard.server.brewnotebackend.global.bootstrap;

import com.pard.server.brewnotebackend.domain.member.Member;
import com.pard.server.brewnotebackend.domain.member.MemberRepository;
import com.pard.server.brewnotebackend.domain.member.RoleType;
import lombok.RequiredArgsConstructor;
import lombok.Value;
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

        if(memberRepository.existsByRole(RoleType.ADMIN)) return;

        Member admin = Member.createActive(
                null,
                null,
                adminEmail,
                passwordEncoder.encode(adminPassword),
                "Admin",
                "admin",
                RoleType.ADMIN
        );

        memberRepository.save(admin);
    }
}
