package com.pard.server.brewnotebackend.domain.recipe;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    /*
    오리지널 레시피 추가 (ADMIN 버전)
    franchiseID추가 (어디서 추가? -> 프론트에서 받아와야 할 것 같음)
    cafeId = null
    originalId = null
    작성자 = ADMINID

    title: 메뉴 이름
    category: 메뉴 이름 (선택할 수 있게 보내줘야 하나 프론트쪽에?)
    thumnailUrl: 일단 지금은 null
    isSignature: null
    isNew: null
    isHidden: false

    recipeOptions
    //프론트에서 Option Value는 일대다로!
    steps -> List로 입력받아서 단방향으로 추가하기 (이때 현재 레시피를 참조)
    */

    //레시피를 등록하기 전, 미리 받아와야할 form data
    @GetMapping("/admin/recipes/form-data")
    //TODO  @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RecipeFormDataResponse> getRecipeFormData() {
        return ResponseEntity.ok(recipeService.getFormData());
    }


    //등록된 레시피 전체 조회 (검색 기능 추가해야 함)

}
