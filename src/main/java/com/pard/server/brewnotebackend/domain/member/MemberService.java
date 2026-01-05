package com.pard.server.brewnotebackend.domain.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {

    void createOwnerWithCafe(OwnerCreateRequest request);

    Page<OwnerSummaryResponse> getOwners(Pageable pageable);

    OwnerDetailResponse getOwner(String ownerId);

    void updateMember(String memberId, MemberUpdateRequest request);

    void createStaff(StaffCreateRequest request);
}
