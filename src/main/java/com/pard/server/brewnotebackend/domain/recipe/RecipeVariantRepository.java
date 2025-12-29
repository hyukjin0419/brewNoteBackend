package com.pard.server.brewnotebackend.domain.recipe;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecipeVariantRepository extends JpaRepository<RecipeVariant, Long> {

    List<RecipeVariant> findByRecipeIdAndIsHiddenFalse(UUID recipeId);
    List<RecipeVariant> findByRecipeIdInAndIsHiddenFalse(List<UUID> recipeIds);
}
