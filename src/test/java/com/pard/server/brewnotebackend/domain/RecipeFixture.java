package com.pard.server.brewnotebackend.domain;

import com.pard.server.brewnotebackend.domain.recipe.Recipe;
import com.pard.server.brewnotebackend.domain.recipe.RecipeCategory;

import java.util.UUID;

public final class RecipeFixture {

    private RecipeFixture() {}

    public static Recipe americano() {
        Recipe recipe = Recipe.builder()
                .id(UUID.randomUUID())
                .title("아메리카노")
                .titleInitial("ㅇㅁㄹㅋㄴ")
                .category(RecipeCategory.COFFEE)
                .thumbnailUrl("americano.png")
                .isSignature(true)
                .isNew(false)
                .build();


        return recipe;
    }

    public static Recipe latte() {
        return Recipe.builder()
                .id(UUID.randomUUID())
                .title("카페라떼")
                .titleInitial("ㅋㅍㄹㄸ")
                .category(RecipeCategory.COFFEE)
                .isSignature(false)
                .isNew(false)
                .build();
    }
}
