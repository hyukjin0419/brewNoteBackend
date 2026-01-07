package com.pard.server.brewnotebackend.domain.member;

import com.pard.server.brewnotebackend.domain.cafe_member.OwnersCafesResponse;
import com.pard.server.brewnotebackend.global.security.currentUser.CurrentUser;
import com.pard.server.brewnotebackend.global.security.currentUser.CustomUserDetails;
import com.pard.server.brewnotebackend.global.utils.UuidUtils;
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
    @PostMapping("/admin/owners")
    //Onwer은 Create시 동시에 활성화 된다. -> 이것도 이메일 발송 시켜서 직접 입력하는 형식으로 하자!!
    public ResponseEntity<Void> createOwner(
            @RequestBody OwnerCreateRequest request
    ) {
        memberService.createOwnerWithCafe(request);
        return ResponseEntity.ok().build();
    }

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

    @GetMapping("/admin/owners/{ownerId}")
    public ResponseEntity<OwnerDetailResponse> getOwner(@PathVariable String ownerId){
        return ResponseEntity.ok(memberService.getOwner(ownerId));
    }

    @PutMapping("/admin/member/{memberId}")
    public ResponseEntity<Void> updateMember(@PathVariable String memberId, @RequestBody MemberUpdateRequest request) {
        memberService.updateMember(memberId, request);
        return ResponseEntity.ok().build();
    }


    //======================= OWNER ========================//
    @GetMapping("/owner/cafes")
    public ResponseEntity<OwnersCafesResponse> getOwnersCafes(
            @CurrentUser CustomUserDetails user
    )  {
        return ResponseEntity.ok(memberService.getOwnersCafes(user.getMemberId()));
    }

    @PostMapping("/owner/staffs")
    public ResponseEntity<Void> createStaff(
            @CurrentUser CustomUserDetails user,
            @RequestBody StaffCreateRequest request
    ) {
        memberService.createStaff(user.getMemberId(), request);
        return ResponseEntity.ok().build();
    }

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
    //======================= STAFF ========================//
}
/*
/admin/owners     → admin만 가능 (admin -> owners를 생성 status pending)
/owners/staff     → owner만 가능 (owners이 staff를 생선한다 -> staff가 계정을 활성화 시킨다. - 계정의 활성화는...여기서 하자!
 */

