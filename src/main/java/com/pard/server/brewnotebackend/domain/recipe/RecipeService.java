package com.pard.server.brewnotebackend.domain.recipe;

import java.util.List;
import java.util.UUID;

public interface RecipeService {
    void createRecipe(RecipeCreateRequest request);

    RecipeFormDataResponse getFormData();

    List<RecipeSearchResponse> search(String keyword);

    RecipeDetailResponse getRecipeDetail(UUID recipeId);
}
