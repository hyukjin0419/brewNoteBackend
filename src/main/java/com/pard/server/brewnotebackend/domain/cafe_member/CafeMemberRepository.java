package com.pard.server.brewnotebackend.domain.cafe_member;

import com.pard.server.brewnotebackend.domain.cafe.Cafe;
import com.pard.server.brewnotebackend.domain.member.MemberRoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CafeMemberRepository extends JpaRepository<CafeMember,Long> {

    List<CafeMember> findByMemberId(UUID memberId);

    boolean existsByMemberIdAndCafeIdAndRole(UUID memberId, UUID cafeId, CafeMemberRoleType roleType);

    Page<CafeMember> findByCafeId(UUID cafeId, Pageable pageable);

    List<CafeMember> findByMemberIdAndRole(UUID memberId, CafeMemberRoleType roleType);

    Optional<CafeMember> findByMemberIdAndCafeIdAndRole(
            UUID memberId,
            UUID cafeId,
            CafeMemberRoleType role
    );

    Optional<CafeMember> findByMemberIdAndCafeId(
            UUID memberId,
            UUID cafeId
    );


}
