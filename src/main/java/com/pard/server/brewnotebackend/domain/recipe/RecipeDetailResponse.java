package com.pard.server.brewnotebackend.domain.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDetailResponse {

    private UUID recipeId;
    private String title;
    private String category;

    private List<VariantResponse> variants;

    public static RecipeDetailResponse from(
            UUID recipeId,
            String title,
            String category,
            List<VariantResponse> variants
    ) {
        return RecipeDetailResponse.builder()
                .recipeId(recipeId)
                .title(title)
                .category(category)
                .variants(variants)
                .build();
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantResponse{
        private Long variantId;
        private RecipeOptionType type;
        private boolean isDefault;
        private List<String> steps;

        public static VariantResponse from(
                Long variantId,
                RecipeOptionType type,
                boolean isDefault,
                List<String> steps
        ) {
            return RecipeDetailResponse.VariantResponse.builder()
                    .variantId(variantId)
                    .type(type)
                    .isDefault(isDefault)
                    .steps(steps)
                    .build();
        }
    }
}
