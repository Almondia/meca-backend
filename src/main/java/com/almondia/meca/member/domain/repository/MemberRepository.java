package com.almondia.meca.member.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.infra.MemberQueryDslRepository;

public interface MemberRepository extends JpaRepository<Member, Id>, MemberQueryDslRepository {
	Optional<Member> findByOauthId(String oAuthId);
}