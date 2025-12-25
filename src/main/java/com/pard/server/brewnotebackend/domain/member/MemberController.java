package com.pard.server.brewnotebackend.domain.member;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "멤버관련 (가입 포함) API")
@Slf4j
@RestController
@RequestMapping("api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/admin/owners")
    //TODO 권한 추가
    //  @PreAuthorize("hasRole('ADMIN')")
    //Onwer은 Create시 동시에 활성화 된다.
    public ResponseEntity<Void> createOwner(
            @RequestBody CreateOwnerRequest request
    ) {
        memberService.createOwnerWithCafe(request);
        return ResponseEntity.ok().build();
    }
}
/*
/admin/owners     → admin만 가능 (admin -> owners를 생성 status pending)
/owners/staff     → owner만 가능 (owners이 staff를 생선한다 -> staff가 계정을 활성화 시킨다. - 계정의 활성화는...여기서 하자!
 */

