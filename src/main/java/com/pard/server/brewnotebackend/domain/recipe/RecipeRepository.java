package com.pard.server.brewnotebackend.domain.recipe;

import com.pard.server.brewnotebackend.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
}
