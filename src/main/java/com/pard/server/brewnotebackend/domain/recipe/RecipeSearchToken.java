package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.global.utils.HangulUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeSearchToken {

    private String raw;
    private String initialSequence;
    private String hangulPrefix;

    public static RecipeSearchToken from(String keyword) {
        String trimmed = keyword.trim();

        return RecipeSearchToken.builder()
                .raw(trimmed)
                .initialSequence(HangulUtils.extractInitialSequence(trimmed))
                .hangulPrefix(HangulUtils.extractHangulPrefix(trimmed))
                .build();
    }

    public boolean hasInitial() {
        return !initialSequence.isEmpty();
    }

    public boolean hasHangulPrefix() {
        return !hangulPrefix.isEmpty();
    }

}

