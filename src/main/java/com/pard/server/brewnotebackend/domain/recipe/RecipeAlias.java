package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.domain.common.BaseEntity;
import com.pard.server.brewnotebackend.global.utils.InitialExtractor;
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
@Table(indexes = {
        @Index(name = "idx_alias_recipe_id", columnList = "recipe_id"),
        @Index(name = "idx_alias_initial", columnList = "alias_initial")
})
public class RecipeAlias extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String alias;

    @Column(nullable = false)
    private String aliasInitials;

    public static RecipeAlias of(String alias) {
        return RecipeAlias.builder()
                .alias(alias)
                .aliasInitials(InitialExtractor.getInitial(alias))
                .build();
    }
}
