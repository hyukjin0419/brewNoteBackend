package com.pard.server.brewnotebackend.domain.cafe_member;

import com.pard.server.brewnotebackend.domain.cafe.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CafeMemberRepository extends JpaRepository<CafeMember,Long> {
}
