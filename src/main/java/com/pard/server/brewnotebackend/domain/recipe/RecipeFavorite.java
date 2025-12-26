package com.pard.server.brewnotebackend.domain.recipe;

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
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "recipe_id"})
        }
)
public class RecipeFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BINARY(16)")
    private UUID memberId;

    @Column(columnDefinition = "BINARY(16)")
    private UUID recipeId;
}
