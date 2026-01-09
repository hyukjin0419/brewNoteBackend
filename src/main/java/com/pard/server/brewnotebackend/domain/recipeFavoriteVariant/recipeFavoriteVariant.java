package com.pard.server.brewnotebackend.domain.recipeFavoriteVariant;

import com.pard.server.brewnotebackend.domain.cafe_member.CafeMember;
import com.pard.server.brewnotebackend.domain.common.BaseEntity;
import com.pard.server.brewnotebackend.domain.recipe.Recipe;
import com.pard.server.brewnotebackend.domain.recipe.RecipeVariant;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "cafe_member_id",
                                "recipe_id",
                                "recipe_variant_id"
                        }
                )
        }
)
public class recipeFavoriteVariant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 누가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_member_id", nullable = false)
    private CafeMember cafeMember;

    // 어떤 레시피
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    // 어떤 조합(핫/아이스/옵션)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_variant_id", nullable = false)
    private RecipeVariant recipeVariant;
}
