package com.pard.server.brewnotebackend.global.bootstrap;

import com.pard.server.brewnotebackend.domain.francise.Franchise;
import com.pard.server.brewnotebackend.domain.francise.FranchiseRepository;
import com.pard.server.brewnotebackend.domain.member.Member;
import com.pard.server.brewnotebackend.domain.member.MemberRepository;
import com.pard.server.brewnotebackend.domain.member.MemberRoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminBootstrapServiceImpl implements AdminBootstrapService {

    private final MemberRepository memberRepository;
    private final FranchiseRepository franchiseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
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

    @Override
    public void initEdiyaFranchise() {
        String name = "이디야";
        Franchise ediya = Franchise.of(name);

        if(franchiseRepository.existsByName(name)) return;

        franchiseRepository.save(ediya);
    }
}
