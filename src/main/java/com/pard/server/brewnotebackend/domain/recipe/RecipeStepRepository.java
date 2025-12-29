package com.pard.server.brewnotebackend.domain.recipe;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {
    List<RecipeStep> findByVariantIdOrderByStepOrderAsc(Long variantId);

    List<RecipeStep> findByVariantIdInOrderByStepOrderAsc(List<Long> variantIds);

}
