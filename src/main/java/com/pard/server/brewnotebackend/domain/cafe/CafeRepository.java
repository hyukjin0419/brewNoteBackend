package com.pard.server.brewnotebackend.domain.cafe;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CafeRepository extends JpaRepository<Cafe, UUID> {

}
