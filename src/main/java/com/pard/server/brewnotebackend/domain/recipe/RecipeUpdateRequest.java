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
public class RecipeUpdateRequest {
    private String title;
    private String category;
    private Boolean isNew;
    private String hotThumbnailUrl;
    private String iceThumbnailUrl;
    private List<String> alias;

    private List<RecipeCreateRequest.VariantRequest> variants;
}
