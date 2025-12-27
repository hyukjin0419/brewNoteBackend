package com.pard.server.brewnotebackend.domain.recipe;

public interface RecipeService {
    void createRecipe(RecipeCreateRequest request);

    RecipeFormDataResponse getFormData();
}
