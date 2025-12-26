package com.pard.server.brewnotebackend.domain.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {

    void createOwnerWithCafe(CreateOwnerRequest request);

    Page<OwnerDetailResponse> getMemberOwners(Pageable pageable);
}
