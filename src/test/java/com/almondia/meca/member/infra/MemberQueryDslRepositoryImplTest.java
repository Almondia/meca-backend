package com.almondia.meca.member.infra;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.repository.MemberRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, QueryDslConfiguration.class})
class MemberQueryDslRepositoryImplTest {

	@Autowired
	EntityManager em;

	@Autowired
	MemberRepository memberRepository;

	@Test
	@DisplayName("삭제되지 않은 멤버를 반환한다.")
	void shouldReturnNotDeletedMember() {
		// given
		final Id memberId1 = Id.generateNextId();
		final Id memberId2 = Id.generateNextId();
		final Id memberId3 = Id.generateNextId();
		Member member1 = MemberTestHelper.generateMember(memberId1);
		Member member2 = MemberTestHelper.generateMember(memberId2);
		Member member3 = MemberTestHelper.generateMember(memberId3);
		member3.delete();
		persistAll(member1, member2, member3);

		// when
		Map<Id, Member> memberMapByIds = memberRepository.findMemberMapByIds(List.of(memberId1, memberId2, memberId3));

		// then
		assertThat(memberMapByIds).hasSize(2);
	}

	private void persistAll(Object... objects) {
		for (Object object : objects) {
			em.persist(object);
		}
	}
}