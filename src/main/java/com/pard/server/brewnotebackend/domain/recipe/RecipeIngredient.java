package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class RecipeIngredient extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    //등록된 재료일 수 도 있고 (Link), 없을 수 도 있음 (Null)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id, nullable = true")
    private Ingredient ingredient;

    @Column(nullable = false)
    private String name; //화면에 보여질 이름

    @Column(nullable = false)
    private String amount; //"20ml"

    @Builder.Default
    private boolean isOptional = false;

    //양방향 관계 명심!
    public void setThisRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public static RecipeIngredient createdLinked(Ingredient ingredient, String amount) {
        return RecipeIngredient.builder()
                .ingredient(ingredient)
                .name(ingredient.getName())
                .amount(amount)
                .build();
    }

    public static RecipeIngredient createCustom(String customName, String amount) {
        return RecipeIngredient.builder()
                .ingredient(null)
                .name(customName)
                .amount(amount)
                .build();
    }
}
