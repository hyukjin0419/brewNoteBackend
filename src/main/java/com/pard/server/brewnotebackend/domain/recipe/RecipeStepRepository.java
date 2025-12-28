package com.pard.server.brewnotebackend.domain.recipe;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {
    List<RecipeStep> findByVariantIdOrderByStepOrderAsc(Long variantId);

}
