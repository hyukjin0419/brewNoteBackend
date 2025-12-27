package com.pard.server.brewnotebackend.domain.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeEnumOptionResponse {
    private String key; //COFFEE
    private String label; //커피

    public static RecipeEnumOptionResponse fromEnum(RecipeCategory category) {
        return RecipeEnumOptionResponse.builder()
                .key(category.getKey())
                .label(category.getLabel())
                .build();
    }
}
