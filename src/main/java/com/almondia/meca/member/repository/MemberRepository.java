package com.almondia.meca.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Id> {
	Optional<Member> findByOauthId(String oAuthId);
}