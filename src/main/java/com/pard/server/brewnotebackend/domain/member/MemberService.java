package com.pard.server.brewnotebackend.domain.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MemberService {

    void createOwnerWithCafe(OwnerCreateRequest request);

    Page<OwnerSummaryResponse> getOwners(Pageable pageable);

    OwnerDetailResponse getOwner(String ownerId);

    void updateMember(String memberId, MemberUpdateRequest request);

    void createStaff(UUID memberId, StaffCreateRequest request);

    Page<StaffSummaryResponse> getStaffs(UUID memberId, UUID cafeId, Pageable pageable);
}
