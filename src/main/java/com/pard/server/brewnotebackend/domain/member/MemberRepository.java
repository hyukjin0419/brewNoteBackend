package com.pard.server.brewnotebackend.domain.member;

import com.pard.server.brewnotebackend.domain.cafe_member.CafeMemberRoleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {
    boolean existsByRole(MemberRoleType roleType);
    boolean existsByEmail(String email);

    @Query("""
        select new com.pard.server.brewnotebackend.domain.member.OwnerDetailResponse(
            m.id,
            m.email,
            m.name,
            m.phoneNumber,
            cast(m.status as string),
            c.id,
            c.name
        )
        from Member m
        left join CafeMember cm on cm.memberId = m.id
            and cm.role = :cafeMemberRole
            and cm.id = (
                select min(cm2.id)
                from CafeMember cm2
                where cm2.memberId = m.id
                and cm2.role = :cafeMemberRole
            )
        left join Cafe c on cm.cafeId = c.id
        where m.role = :memberRole
    """)
    // MVP에서는 제일 처음 생성된 카페
    Page<OwnerDetailResponse> findOwnerDetailsWithRepresentativeCafe (
            @Param("memberRole") MemberRoleType memberRoleType,
            @Param("cafeMemberRole") CafeMemberRoleType cafeMemberRoleType,
            Pageable pageable
    );
}
