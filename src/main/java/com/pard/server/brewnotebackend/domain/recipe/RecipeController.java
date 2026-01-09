package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.global.utils.UuidUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Recipe", description = "레시피 관련 API")
@Slf4j
@RestController
@RequestMapping("api/recipe")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    /*
    매장 레시피 관리하는 거
     */

    //등록
    //======================= ADMIN ========================//
    @Operation(
            summary = "레시피 등록",
            description = "관리자가 새로운 레시피를 등록합니다."
    )
    @PostMapping("/admin/recipe")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createRecipe(@RequestBody RecipeCreateRequest request) {
        recipeService.createRecipe(request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "레시피 등록용 폼 데이터 조회",
            description = "레시피 등록에 필요한 기본 데이터(카테고리, 옵션 등)를 조회합니다."
    )
    @GetMapping("/admin/recipes/form-data")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecipeFormDataResponse> getRecipeFormData() {
        return ResponseEntity.ok(recipeService.getFormData());
    }

    @Operation(
            summary = "레시피 수정",
            description = "관리자가 기존 레시피 정보를 수정합니다."
    )
    @PutMapping("/admin/recipe/{recipeId}")
    public ResponseEntity<Void> updateRecipe(@PathVariable String recipeId, @RequestBody RecipeUpdateRequest request) {
        recipeService.updateRecipe(recipeId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "레시피 삭제",
            description = "관리자가 레시피를 삭제합니다."
    )
    @DeleteMapping("/admin/recipe/{recipeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRecipe(@PathVariable String recipeId) {
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.ok().build();
    }

    //======================= ALL ========================//
    @Operation(
            summary = "레시피 검색",
            description = "메뉴 이름 및 초성을 기준으로 레시피를 검색합니다."
    )
    @GetMapping("/search/recipes")
    public List<RecipeSearchResponse> search(@RequestParam String keyword) {
        return recipeService.search(keyword);
    }

    @Operation(
            summary = "레시피 목록 조회",
            description = "프랜차이즈, 카테고리, 신규 여부 조건에 따라 레시피 목록을 조회합니다."
    )
    @GetMapping("/recipes")
    public List<RecipeDetailResponse> getRecipes(
            @RequestParam(required = false) String franchiseId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean isNew
    ) {
        RecipeDetailRequest request = RecipeDetailRequest.of(franchiseId, category, isNew);
        return recipeService.getRecipes(request);
    }

    @Operation(
            summary = "레시피 상세 조회",
            description = "특정 레시피의 상세 정보를 조회합니다."
    )
    @GetMapping("/{recipeId}")
    public RecipeDetailResponse getRecipeDetail(@PathVariable String recipeId) {
        return recipeService.getRecipeDetail(UuidUtils.parse(recipeId));
    }

}
