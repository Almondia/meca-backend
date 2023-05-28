package com.almondia.meca.member.domain.entity;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.test.context.TestPropertySource;

import com.almondia.meca.common.configuration.jpa.JpaAuditingConfiguration;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.member.domain.vo.Email;
import com.almondia.meca.member.domain.vo.Name;
import com.almondia.meca.member.domain.vo.OAuthType;
import com.almondia.meca.member.domain.vo.Role;

/**
 * meta데이터를 통해 entity 속성이 잘 생성되었는지 테스트
 * 영속화시 Member에 날짜가 자동으로 갱신되는지 테스트
 * updateName시 name 변화 테스트
 * updateProfile시 profile 변화 테스트
 */
@DataJpaTest
@TestPropertySource(properties = {"spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({JpaAuditingConfiguration.class, QueryDslConfiguration.class})
class MemberTest {

	@PersistenceContext
	private EntityManager entityManager;

	@Test
	@DisplayName("유저 속성이 잘 생성되었는지 검증")
	void memberEntityCreationTest() {
		EntityType<?> entityType = entityManager.getMetamodel().entity(Member.class);
		assertThat(entityType).isNotNull();
		assertThat(entityType.getName()).isEqualTo("Member");
		assertThat(entityType.getAttributes()).extracting("name")
			.containsExactlyInAnyOrder("profile", "oauthId", "memberId", "name", "email", "oAuthType", "createdAt",
				"modifiedAt",
				"role", "isDeleted");
	}

	@Test
	@DisplayName("entity를 생성해서 저장시 createdAt과 modifiedAt이 자동으로 업데이트되며 서로 같음")
	void shouldUpdateCreatedAtAndModifiedAtAndTheyAreEqualWhenEntitySave() {
		JpaRepository<Member, Id> memberRepository = new SimpleJpaRepository<>(Member.class, entityManager);
		Member member = Member.builder()
			.memberId(Id.generateNextId())
			.oauthId("id")
			.email(new Email("hello@naver.com"))
			.name(Name.of("hello"))
			.oAuthType(OAuthType.GOOGLE)
			.role(Role.USER)
			.isDeleted(false)
			.build();
		memberRepository.save(member);
		Member result = memberRepository.findById(member.getMemberId()).orElseThrow();
		LocalDateTime createdAt = result.getCreatedAt();
		LocalDateTime modifiedAt = result.getModifiedAt();
		assertThat(createdAt).isEqualTo(modifiedAt);
	}

	@Test
	@DisplayName("updateName시 name 변화 테스트")
	void shouldUpdateNameWhenUpdateName() {
		Member member = MemberTestHelper.generateMember(Id.generateNextId());
		member.updateName(Name.of("newName"));
		assertThat(member.getName()).isEqualTo(Name.of("newName"));
	}

	@Test
	@DisplayName("updateProfile시 profile 변화 테스트")
	void shouldUpdateProfileWhenUpdateProfile() {
		Member member = MemberTestHelper.generateMember(Id.generateNextId());
		member.updateProfile(new Image("aws"));
		assertThat(member.getProfile()).isEqualTo(new Image("aws"));
	}
}