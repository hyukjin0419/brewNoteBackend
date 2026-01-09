package com.pard.server.brewnotebackend.domain.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDetailRequest {
    private String franchiseId;
    private String category;
    private Boolean isNew;

    public static RecipeDetailRequest of(
            String franchiseId,
            String category,
            Boolean isNew
    ) {
        return new RecipeDetailRequest(franchiseId, category, isNew);
    }
}
