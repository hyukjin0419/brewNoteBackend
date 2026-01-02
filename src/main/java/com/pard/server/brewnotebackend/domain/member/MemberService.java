package com.pard.server.brewnotebackend.domain.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface MemberService {

    void createOwnerWithCafe(CreateOwnerRequest request);

    Page<OwnerSummaryResponse> getMemberOwners(Pageable pageable);

    OwnerDetailResponse getMemberOwner(String ownerId);
}
