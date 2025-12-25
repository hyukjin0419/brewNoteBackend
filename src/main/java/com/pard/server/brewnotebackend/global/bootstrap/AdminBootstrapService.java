package com.pard.server.brewnotebackend.global.bootstrap;

import com.pard.server.brewnotebackend.domain.member.MemberRepository;

public interface AdminBootstrapService {

    void initAdminIfNotExists(String adminEmail, String adminPassword);
    void initEdiyaFranchise();

}
