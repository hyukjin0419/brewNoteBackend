package com.pard.server.brewnotebackend.domain.francise;

import com.pard.server.brewnotebackend.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FranchiseRepository extends JpaRepository<Franchise,String> {
}
