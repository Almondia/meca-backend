package com.almondia.meca.member.infra;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.entity.QMember;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class MemberQueryDslRepositoryImpl implements MemberQueryDslRepository {

	private static final QMember member = QMember.member;

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Map<Id, Member> findMemberMapByIds(Collection<Id> memberIds) {
		List<Member> members = jpaQueryFactory.selectFrom(member)
			.where(member.memberId.in(memberIds),
				member.isDeleted.eq(false))
			.fetch();
		return members.stream()
			.collect(Collectors.toMap(Member::getMemberId, member -> member));
	}
}
