package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.global.utils.UuidUtils;
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
    //TODO: ADMIN APIs 권한 추가
    @PostMapping("/admin/recipe")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> createRecipe(@RequestBody RecipeCreateRequest request) {
        recipeService.createRecipe(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/admin/recipes/form-data")
//    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecipeFormDataResponse> getRecipeFormData() {
        return ResponseEntity.ok(recipeService.getFormData());
    }

    @PutMapping("/admin/recipe/{recipeId}")
    public ResponseEntity<Void> updateRecipe(@PathVariable String recipeId, @RequestBody RecipeUpdateRequest request) {
        recipeService.updateRecipe(recipeId, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/admin/recipe/{recipeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRecipe(@PathVariable String recipeId) {
        recipeService.deleteRecipe(recipeId);
        return ResponseEntity.ok().build();
    }

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
