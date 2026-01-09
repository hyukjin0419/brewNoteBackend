package com.pard.server.brewnotebackend.domain.recipe;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {

    @Query("""
    select distinct r
    from Recipe r
    left join RecipeAlias a
        on a.recipeId = r.id
    where
        (
            :initialKeyword is not null
            and (
                r.titleInitial like :initialKeyword
                or a.aliasInitials like :initialKeyword
            )
        )
    or
        (
            :prefixKeyword is not null
            and (
                r.title like :prefixKeyword
                or a.alias like :prefixKeyword
            )
        )
    or
        (
            :containsKeyword is not null
            and (
                r.title like :containsKeyword
                or a.alias like :containsKeyword
            )
        )
""")
    List<Recipe> searchCandidates(
            @Param("initialKeyword") String initialKeyword,
            @Param("prefixKeyword") String prefixKeyword,
            @Param("containsKeyword") String containsKeyword,
            Pageable pageable
    );





    boolean existsByTitleAndFranchiseId(String title, UUID franchiseId);

    List<Recipe> findByFranchiseIdAndCategoryAndIsHiddenFalseOrderByTitleAsc(UUID franchiseId, RecipeCategory category);

    List<Recipe> findByFranchiseIdAndNewTrueAndIsHiddenFalseOrderByCreatedAtDesc(UUID franchiseId);
}
