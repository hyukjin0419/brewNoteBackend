package com.pard.server.brewnotebackend.domain.recipeFavoriteVariant;

import com.pard.server.brewnotebackend.global.security.currentUser.CurrentUser;
import com.pard.server.brewnotebackend.global.security.currentUser.CustomUserDetails;
import com.pard.server.brewnotebackend.global.utils.UuidUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Recipe_Favorites", description = "레시피 즐겨찾기 관련 API")
@Slf4j
@RestController
@RequestMapping("/api/recipe/recipe-favorites")
@RequiredArgsConstructor
public class RecipeFavoriteController {

    private final RecipeFavoriteService removeFavoriteService;

    @Operation(
            summary = "레시피 즐겨찾기 토글",
            description = "카페에 소속된 사용자가 특정 레시피 옵션을 즐겨찾기에 추가하거나 제거합니다."
    )
    @PostMapping("/toggle")
    public ResponseEntity<ToggleResponse> toggleFavorite(
            @CurrentUser CustomUserDetails user,
            @RequestBody RecipeFavoriteVariantRequest request
    ) {
        ToggleResponse response = ToggleResponse.from(removeFavoriteService.toggleFavorite(user.getMemberId(), request));
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "즐겨찾기 목록 조회",
            description = "카페 기준으로 즐겨찾기에 추가된 레시피 옵션 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<RecipeFavoriteListResponse> getFavorites(
            @CurrentUser CustomUserDetails user,
            @RequestParam String cafeId
    ) {
        return ResponseEntity.ok(
                removeFavoriteService.getFavorites(user.getMemberId(), UuidUtils.parse(cafeId)));
    }
}