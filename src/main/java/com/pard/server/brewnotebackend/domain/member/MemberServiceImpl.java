package com.pard.server.brewnotebackend.domain.member;

import com.pard.server.brewnotebackend.domain.cafe.Cafe;
import com.pard.server.brewnotebackend.domain.cafe.CafeRepository;
import com.pard.server.brewnotebackend.domain.cafe_member.CafeMember;
import com.pard.server.brewnotebackend.domain.cafe_member.CafeMemberRepository;
import com.pard.server.brewnotebackend.domain.cafe_member.CafeMemberRoleType;
import com.pard.server.brewnotebackend.domain.cafe_member.OwnersCafesResponse;
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
    @Transactional
    //owner이 생성될때 owner의 카페도 같이 생성되어야 한다.
    public void createOwnerWithCafe(OwnerCreateRequest request) {
        validateDuplicateMember(request.getEmail());

        //TODO 이메일 도입하면 Pw 빼야 함.
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
    public Page<OwnerSummaryResponse> getOwners(Pageable pageable) {

        return memberRepository.findOwnerDetailsWithRepresentativeCafe(MemberRoleType.USER, CafeMemberRoleType.OWNER, pageable);
    }

    @Override
    public OwnerDetailResponse getOwner(String ownerId) {
        UUID ownerUuid = UuidUtils.parse(ownerId);

        Member member = memberRepository.findById(ownerUuid)
                .orElseThrow(() -> new EntityNotFoundException("OWNER을 찾을 수 없습니다."));

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

    @Override
    @Transactional
    public void updateMember(String memberId, MemberUpdateRequest request) {
        UUID memberUuid = UuidUtils.parse(memberId);

        Member member = memberRepository.findById(memberUuid)
                .orElseThrow(() -> new EntityNotFoundException("Member을 찾을 수 없습니다."));

        if(request.getEmail() != null) {
            member.updateEmail(request.getEmail());
        }

        if(request.getName() != null) {
            member.updateName(request.getName());
        }

        if (request.getPhoneNumber() != null) {
            member.updatePhoneNumber(request.getPhoneNumber());
        }

        if (request.getStatus() != null) {
            if (MemberStatus.valueOf(request.getStatus()) == MemberStatus.ACTIVE) {
                member.activate();
            } else {
                member.deactivate();
            }
        }
    }

    // ========================== ONWER ============================== //
    @Override
    @Transactional
    public void createStaff(UUID ownerId, StaffCreateRequest request) {

        if(!cafeMemberRepository.existsByMemberIdAndCafeIdAndRole(ownerId, UuidUtils.parse(request.getCafeId()),CafeMemberRoleType.OWNER)){
            throw new IllegalStateException("카페 오너 권한이 없습니다.");
        };

        //TODO 이메일 도입 후 바꿔야 함
        validateDuplicateMember(request.getEmail());

        String tempPassword = "1234";
        String encodedPassword = passwordEncoder.encode(tempPassword);

        Member staff = request.toMemberEntity();
        memberRepository.save(staff);

        UUID cafeUuid = UuidUtils.parse(request.getCafeId());
        cafeRepository.findById(cafeUuid)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카페 Id 입니다."));

        CafeMember cafeMember = CafeMember.createStaff(cafeUuid, staff.getId());
        cafeMemberRepository.save(cafeMember);
    }

    @Override
    public Page<StaffSummaryResponse> getStaffs(UUID ownerId, UUID cafeId, Pageable pageable) {

        //ownerId를 해당 카페의 소속된 cafeMember을 전부 찾아서 해당 카페의 cafeMember을 통해 member정보와 cafemember를 종합해서 받아와야 함
        if(!cafeMemberRepository.existsByMemberIdAndCafeIdAndRole(ownerId, cafeId,CafeMemberRoleType.OWNER)){
            throw new IllegalStateException("카페 오너 권한이 없습니다.");
        };

        Page<CafeMember> cafeMembers = cafeMemberRepository.findByCafeId(cafeId, pageable);

        List<UUID> memberIds = cafeMembers.getContent().stream()
                .map(CafeMember::getMemberId)
                .toList();

        Map<UUID, Member> memberMap = memberRepository.findByIdIn(memberIds)
                .stream().collect(Collectors.toMap(Member::getId, Function.identity()));

        return cafeMembers.map(cm -> {
            Member member = memberMap.get(cm.getMemberId());

            if (member == null) {
                throw new IllegalStateException("멤버 정보를 찾을 수 없습니다.");
            }

            return StaffSummaryResponse.from(
                    member.getId().toString(),
                    cm.getCafeId().toString(),
                    cm.getId().toString(),
                    cm.getRole(),
                    cm.getStatus(),
                    member.getName(),
                    member.getNickname(),
                    member.getEmail(),
                    member.getPhoneNumber()
            );
        });
    }

    @Override
    public OwnersCafesResponse getOwnersCafes(UUID memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다"));

        List<CafeMember> ownedCafeMembers = cafeMemberRepository.findByMemberIdAndRole(
                memberId,
                CafeMemberRoleType.OWNER
        );

        List<UUID> cafeIds = ownedCafeMembers.stream()
                .map(CafeMember::getCafeId)
                .toList();

        Map<UUID, Cafe> cafeMap = cafeRepository.findAllById(cafeIds).stream()
                .collect(Collectors.toMap(Cafe::getId, Function.identity()));

        List<OwnersCafesResponse.OwnedCafeSummary> ownedCafes =
                cafeIds.stream()
                        .map(cafeId -> {
                            Cafe cafe = cafeMap.get(cafeId);
                            return OwnersCafesResponse.OwnedCafeSummary.from(
                                    cafe.getId().toString(),
                                    cafe.getName()
                            );
                        }).toList();

        return OwnersCafesResponse.from(
                ownedCafes
        );
    }
}
























