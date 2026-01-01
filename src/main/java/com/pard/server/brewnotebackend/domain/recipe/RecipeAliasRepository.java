package com.pard.server.brewnotebackend.domain.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecipeAliasRepository extends JpaRepository<RecipeAlias, Long> {
    List<RecipeAlias> findByRecipeIdIn(List<UUID> recipeIds);

    List<RecipeAlias> findByRecipeId(UUID recipeId);

    void deleteAllByRecipeId(UUID recipeId);
}
