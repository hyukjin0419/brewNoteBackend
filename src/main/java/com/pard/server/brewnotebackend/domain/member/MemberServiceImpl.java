package com.pard.server.brewnotebackend.domain.member;

import com.pard.server.brewnotebackend.domain.cafe.Cafe;
import com.pard.server.brewnotebackend.domain.cafe.CafeRepository;
import com.pard.server.brewnotebackend.domain.cafe_member.CafeMember;
import com.pard.server.brewnotebackend.domain.cafe_member.CafeMemberRepository;
import com.pard.server.brewnotebackend.domain.cafe_member.CafeMemberRoleType;
import com.pard.server.brewnotebackend.global.exception.BusinessException;
import com.pard.server.brewnotebackend.global.exception.ErrorCode;
import com.pard.server.brewnotebackend.global.utils.UuidUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final CafeRepository cafeRepository;
    private final CafeMemberRepository cafeMemberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional()
    //owner이 생성될때 owner의 카페도 같이 생성되어야 한다.
    public void createOwnerWithCafe(CreateOwnerRequest request) {
        validateDuplicateMember(request.getEmail());

        String tempPassword = "1234";
        String encodedPassword = passwordEncoder.encode(tempPassword);

        Member owner = request.toMemberEntity(encodedPassword);
        memberRepository.save(owner);

        Cafe cafe = request.toCafeEntity();
        cafeRepository.save(cafe);

        CafeMember cafeMember = CafeMember.createCafeOwner(cafe.getId(), owner.getId());
        cafeMemberRepository.save(cafeMember);
    }

    private void validateDuplicateMember(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    @Override
    public Page<OwnerSummaryResponse> getMemberOwners(Pageable pageable) {

        return memberRepository.findOwnerDetailsWithRepresentativeCafe(MemberRoleType.USER, CafeMemberRoleType.OWNER, pageable);
    }

    @Override
    public OwnerDetailResponse getMemberOwner(String ownerId) {
        UUID ownerUuid = UuidUtils.parse(ownerId);

        Member member = memberRepository.findById(ownerUuid)
                .orElseThrow(() -> new EntityNotFoundException("OWNER을 찾을 수 업습니다."));

        List<CafeMember> cafeMembers = cafeMemberRepository.findByMemberId(ownerUuid);

        List<UUID> cafeIds = cafeMembers.stream()
                .map(CafeMember::getCafeId)
                .toList();

        List<Cafe> cafes = cafeRepository.findAllById(cafeIds);

        Map<UUID, Cafe> cafeMap = cafes.stream()
                .collect(Collectors.toMap(Cafe::getId, Function.identity()));

        List<OwnerDetailResponse.CafeSummaryResponse> cafeSummaries =
                cafeMembers.stream()
                        .map(cm -> {
                            Cafe cafe = cafeMap.get(cm.getCafeId());
                            if (cafe == null) return null;
                            return OwnerDetailResponse.CafeSummaryResponse.from(
                                    cafe.getId(),
                                    cafe.getName(),
                                    cafe.getStatus().name(),
                                    cafe.getAddress()
                            );
                        }).filter(Objects::nonNull).toList();

        return OwnerDetailResponse.from(member, cafeSummaries);
    }
}
























