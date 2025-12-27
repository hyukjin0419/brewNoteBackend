package com.pard.server.brewnotebackend.domain.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateRequest {
    private String franchiseId;
    private String title;
    private String category; //ENUM으로 받아야 하나?
    private List<String> recipeOptions;
    private List<String> steps;

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
}
