package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.domain.francise.FranchiseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeFormDataResponse {

    private List<RecipeEnumOptionResponse> recipeCategories;
    private List<FranchiseResponse> franchises;

    public static RecipeFormDataResponse from(List<RecipeEnumOptionResponse> recipeCategories, List<FranchiseResponse> franchises) {
        return RecipeFormDataResponse.builder()
                .recipeCategories(recipeCategories)
                .franchises(franchises)
                .build();
    }

}
