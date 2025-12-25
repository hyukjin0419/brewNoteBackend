package com.pard.server.brewnotebackend.domain.cafe_member;

import com.pard.server.brewnotebackend.domain.cafe.Cafe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeMemberRepository extends JpaRepository<CafeMember,String> {

}
