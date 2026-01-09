package com.pard.server.brewnotebackend.domain.recipeFavoriteVariant;

import com.pard.server.brewnotebackend.domain.common.BaseEntity;
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
                @UniqueConstraint(
                        columnNames = {
                                "cafe_member_id",
                                "recipe_variant_id"
                        }
                )
        }
)
public class RecipeFavoriteVariant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long cafeMemberId;

    @Column(nullable = false)
    private UUID recipeId;

    @Column(nullable = false)
    private Long recipeVariantId;
}
