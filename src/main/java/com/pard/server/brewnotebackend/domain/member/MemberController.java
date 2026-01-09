package com.pard.server.brewnotebackend.domain.member;

import com.pard.server.brewnotebackend.domain.cafe_member.CafesResponse;
import com.pard.server.brewnotebackend.global.security.currentUser.CurrentUser;
import com.pard.server.brewnotebackend.global.security.currentUser.CustomUserDetails;
import com.pard.server.brewnotebackend.global.utils.UuidUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Member", description = "멤버관련 (가입 포함) API")
@Slf4j
@RestController
@RequestMapping("api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    //======================= ADMIN ========================//
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "점주 계정 생성",
            description = "관리자가 점주 계정을 생성하고 카페를 함께 등록합니다."
    )
    @PostMapping("/admin/owners")
    public ResponseEntity<Void> createOwner(
            @RequestBody OwnerCreateRequest request
    ) {
        memberService.createOwnerWithCafe(request);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "점주 목록 조회",
            description = "관리자가 등록된 점주 목록을 페이징 형태로 조회합니다."
    )
    @GetMapping("/admin/owners")
    public ResponseEntity<Page<OwnerSummaryResponse>> getOwners(
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        return ResponseEntity.ok(memberService.getOwners(pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "점주 상세 조회",
            description = "관리자가 특정 점주의 상세 정보를 조회합니다."
    )
    @GetMapping("/admin/owners/{ownerId}")
    public ResponseEntity<OwnerDetailResponse> getOwner(@PathVariable String ownerId){
        return ResponseEntity.ok(memberService.getOwner(ownerId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "멤버 정보 수정",
            description = "관리자가 특정 멤버의 정보를 수정합니다."
    )
    @PutMapping("/admin/member/{memberId}")
    public ResponseEntity<Void> updateMember(@PathVariable String memberId, @RequestBody MemberUpdateRequest request) {
        memberService.updateMember(memberId, request);
        return ResponseEntity.ok().build();
    }


    //======================= OWNER ========================//
     @Operation(
            summary = "점주 소속 카페 조회",
            description = "로그인한 점주가 자신이 관리하는 카페 목록을 조회합니다."
        )
    @GetMapping("/owner/cafes")
    public ResponseEntity<CafesResponse> getOwnersCafes(
            @CurrentUser CustomUserDetails user
    )  {
        return ResponseEntity.ok(memberService.getOwnersCafes(user.getMemberId()));
    }

    @Operation(
            summary = "직원 계정 생성",
            description = "점주가 자신의 카페에 직원을 추가합니다."
    )
    @PostMapping("/owner/staffs")
    public ResponseEntity<Void> createStaff(
            @CurrentUser CustomUserDetails user,
            @RequestBody StaffCreateRequest request
    ) {
        memberService.createStaff(user.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "직원 목록 조회",
            description = "점주가 특정 카페에 소속된 직원 목록을 조회합니다."
    )
    @GetMapping("/owner/staffs")
    public ResponseEntity<Page<StaffSummaryResponse>> getStaffs(
            @CurrentUser CustomUserDetails user,
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable,
            String cafeId
    ) {
        return ResponseEntity.ok(memberService.getStaffs(user.getMemberId(), UuidUtils.parse(cafeId), pageable));
    }

    @Operation(
            summary = "직원 상세 조회",
            description = "점주가 특정 카페의 직원 상세 정보를 조회합니다."
    )
    @GetMapping("/owner/cafes/{cafeId}/staffs/{staffId}")
    public ResponseEntity<StaffDetailResponse> getStaff(
            @CurrentUser CustomUserDetails user,
            @PathVariable String cafeId,
            @PathVariable String staffId
    ){
        return ResponseEntity.ok(memberService.getStaff(user.getMemberId(), UuidUtils.parse(cafeId), UuidUtils.parse(staffId)));
    }

    //======================= STAFF ========================//
    @Operation(
            summary = "직원 소속 카페 조회",
            description = "로그인한 직원이 자신이 소속된 카페 목록을 조회합니다."
    )
    @GetMapping("/staff/cafes")
    public ResponseEntity<CafesResponse> getStaffCafes(
            @CurrentUser CustomUserDetails user
    ) {
        return ResponseEntity.ok(memberService.getCafes(user.getMemberId()));
    }

    //

}
/*
/admin/owners     → admin만 가능 (admin -> owners를 생성 status pending)
/owners/staff     → owner만 가능 (owners이 staff를 생선한다 -> staff가 계정을 활성화 시킨다. - 계정의 활성화는...여기서 하자!
 */

