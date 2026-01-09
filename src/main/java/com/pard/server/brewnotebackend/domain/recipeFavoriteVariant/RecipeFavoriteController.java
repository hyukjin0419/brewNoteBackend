package com.pard.server.brewnotebackend.domain.recipeFavoriteVariant;

import com.pard.server.brewnotebackend.global.security.currentUser.CurrentUser;
import com.pard.server.brewnotebackend.global.security.currentUser.CustomUserDetails;
import com.pard.server.brewnotebackend.global.utils.UuidUtils;
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

    /*
     * 즐겨찾기 추가
     */
    @PostMapping
    public ResponseEntity<Void> addFavorite(
            @CurrentUser CustomUserDetails user,
            @RequestBody RecipeFavoriteVariantRequest.Add request
    ) {
        removeFavoriteService.addFavorite(
                user.getMemberId(),
                request
        );
        return ResponseEntity.ok().build();
    }

    /**
     * 즐겨찾기 삭제
     */
    @DeleteMapping
    public ResponseEntity<Void> removeFavorite(
            @CurrentUser CustomUserDetails user,
            @RequestBody RecipeFavoriteVariantRequest.Remove request
    ) {
        removeFavoriteService.removeFavorite(
                user.getMemberId(),
                request
        );
        return ResponseEntity.ok().build();
    }

    /**
     * 즐겨찾기 목록 조회 (카페 단위)
     */
    @GetMapping
    public ResponseEntity<RecipeFavoriteListResponse> getFavorites(
            @CurrentUser CustomUserDetails user,
            @RequestParam String cafeId
    ) {
        return ResponseEntity.ok(
                removeFavoriteService.getFavorites(
                        user.getMemberId(),
                        UuidUtils.parse(cafeId)
                )
        );
    }
}