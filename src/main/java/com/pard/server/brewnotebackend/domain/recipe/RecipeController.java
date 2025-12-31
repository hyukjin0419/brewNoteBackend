package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.global.utils.UuidUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    //TODO: ADMIN APIs 권한 추가
    //  @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/recipe")
    //TODO AdminId -> 는 @CurrentUser로 넘겨주기: 일단은 서비스단에서 ADMIN으로 하드코딩하기
    public ResponseEntity<Void> createRecipe(@RequestBody RecipeCreateRequest request) {
        recipeService.createRecipe(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/recipes/form-data")
    //TODO  @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecipeFormDataResponse> getRecipeFormData() {
        return ResponseEntity.ok(recipeService.getFormData());
    }

    //TODO update 기능이 추가되어야 할 것 같음!
//    public ResponseEntity<Void> updateRecipe(@RequestBody)

    //======================= ALL ========================//
    //등록된 레시피 전체 조회 (검색 기능 추가해야 함)
    @GetMapping("/search/recipes")
    public List<RecipeSearchResponse> search(@RequestParam String keyword) {
        return recipeService.search(keyword);
    }

    @GetMapping("/recipes")
    public List<RecipeDetailResponse> getRecipes(
            @RequestParam(required = false) String franchiseId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean favorite
    ) {
        RecipeDetailRequest request = RecipeDetailRequest.of(franchiseId, category, favorite);
        return recipeService.getRecipes(request);
    }


    @GetMapping("/{recipeId}")
    public RecipeDetailResponse getRecipeDetail(@PathVariable String recipeId) {
        return recipeService.getRecipeDetail(UuidUtils.parse(recipeId));
    }

}
