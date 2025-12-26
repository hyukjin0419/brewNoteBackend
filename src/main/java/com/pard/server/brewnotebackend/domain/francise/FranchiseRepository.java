package com.pard.server.brewnotebackend.domain.francise;

import com.pard.server.brewnotebackend.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FranchiseRepository extends JpaRepository<Franchise, UUID> {
    boolean existsByName(String name);
}
