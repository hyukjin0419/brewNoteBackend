package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.domain.common.BaseEntity;
import com.pard.server.brewnotebackend.global.utils.HangulUtils;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@SuperBuilder
public class RecipeAlias extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipe_id", columnDefinition = "BINARY(16)", nullable = false)
    private UUID recipeId;


    @Column(nullable = false)
    private String alias;

    @Column(nullable = false)
    private String aliasInitials;

    public static RecipeAlias of(String alias) {
        return RecipeAlias.builder()
                .alias(alias)
                .aliasInitials(HangulUtils.extractInitialSequence(alias))
                .build();
    }
}
