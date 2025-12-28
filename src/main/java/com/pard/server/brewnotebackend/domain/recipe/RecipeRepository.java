package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.domain.member.Member;
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
            r.titleInitial like :initialKeyWord
        or
            a.aliasInitials like :initialKeyWord
        or(
            :prefixKeyword is not null
            and (
                r.title like :prefixKeyword
            or  a.alias like :prefixKeyword
            )
        )
    """)
    List<Recipe> searchCandidates(
            @Param("initialKeyWord") String initialKeyWord,
            @Param("prefixKeyword") String prefixKeyword,
            Pageable pageable
    );
}
