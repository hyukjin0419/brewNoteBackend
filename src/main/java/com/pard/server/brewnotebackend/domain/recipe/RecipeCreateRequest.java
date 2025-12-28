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
    private String category;
    private List<String> alias;

    private List<VariantRequest> variants;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VariantRequest{
        private RecipeOptionType optionType;
        private boolean isDefault;
        private List<String> steps;
    }
}
