package com.almondia.meca.member.application;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import com.almondia.meca.auth.oauth.infra.attribute.OAuth2UserAttribute;
import com.almondia.meca.common.configuration.jpa.QueryDslConfiguration;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.common.domain.vo.Image;
import com.almondia.meca.helper.MemberTestHelper;
import com.almondia.meca.member.controller.dto.MemberDto;
import com.almondia.meca.member.controller.dto.UpdateMemberRequestDto;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.vo.Email;
import com.almondia.meca.member.domain.vo.Name;
import com.almondia.meca.member.domain.vo.OAuthType;
import com.almondia.meca.member.domain.vo.Role;
import com.almondia.meca.member.repository.MemberRepository;

/**
 * saveOAuthAttribute 요청시 성공적으로 회원을 db에 저장해야한다.
 * findMember 요청시 없다면 IllegalArgumentException 예외 출력
 * findMember 요청시 있다면 Member 반환
 * findMyProfile 요청시 findMember와 로직은 동일하지만 Dto 형태로 반환
 * update 요청시 Member의 name만 요청한 경우 name만 수정된다
 * update 요청시 Member의 profile만 요청한 경우 profile만 수정된다
 * update 요청시 모두 변경을 요청한경우 모두 수정된다
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({MemberService.class, QueryDslConfiguration.class})
class MemberServiceTest {

	@Autowired
	MemberRepository memberRepository;

	@Autowired
	MemberService memberService;

	@Test
	@DisplayName("save 요청시 oauth2UserAttribute 정보가 db에 저장된다")
	void shouldSaveDbTestWhenCallSaveOAuthAttributeTest() {
		OAuth2UserAttribute oAuth2UserAttribute = new OAuth2UserAttribute("id", "hello", "marrin1101@naver.com",
			OAuthType.NAVER);
		memberService.save(oAuth2UserAttribute);
		List<Member> all = memberRepository.findAll();
		assertThat(all).isNotEmpty();
	}

	@Test
	@DisplayName("findMember 요청시 없다면 예외 발생")
	void shouldThrowExceptionWhenNotFoundEntityTest() {
		assertThatThrownBy(() -> memberService.findMember(Id.generateNextId()))
			.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	@DisplayName("findMember 요청시 있다면 해당 entity 출력")
	void shouldReturnEntityWhenCallFindMemberTest() {
		Id id = Id.generateNextId();
		Member member = Member.builder()
			.memberId(id)
			.oauthId("id")
			.oAuthType(OAuthType.KAKAO)
			.name(Name.of("hello"))
			.email(new Email("hello@naver.com"))
			.role(Role.USER)
			.build();
		memberRepository.save(member);
		Member result = memberService.findMember(id);
		assertThat(result).hasFieldOrProperty("memberId")
			.hasFieldOrProperty("oAuthType")
			.hasFieldOrProperty("name")
			.hasFieldOrProperty("role")
			.hasFieldOrProperty("email");
	}

	@Test
	@DisplayName("findMyProfile 요청시 findMember와 로직은 동일하지만 Dto 형태로 반환")
	void shouldReturnMemberResponseDtoWHenCallFindMyProfileTest() {
		Id id = Id.generateNextId();
		Member member = Member.builder()
			.memberId(id)
			.oauthId("id")
			.oAuthType(OAuthType.KAKAO)
			.name(Name.of("hello"))
			.email(new Email("hello@naver.com"))
			.role(Role.USER)
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.build();
		memberRepository.save(member);
		MemberDto result = memberService.findMyProfile(id);
		assertThat(result)
			.hasFieldOrPropertyWithValue("memberId", member.getMemberId())
			.hasFieldOrPropertyWithValue("name", member.getName())
			.hasFieldOrPropertyWithValue("email", member.getEmail())
			.hasFieldOrPropertyWithValue("oauthType", member.getOAuthType())
			.hasFieldOrPropertyWithValue("role", member.getRole())
			.hasFieldOrPropertyWithValue("isDeleted", member.isDeleted())
			.hasFieldOrProperty("createdAt")
			.hasFieldOrProperty("modifiedAt");
	}

	@Test
	@DisplayName("update 요청시 Member의 name만 요청한 경우 name만 수정된다")
	void shouldUpdateNameWhenCallUpdateTest() {
		// given
		final Id memberId = Id.generateNextId();
		Member member = MemberTestHelper.generateMember(memberId);
		member = memberRepository.save(member);
		UpdateMemberRequestDto updateMemberRequest = UpdateMemberRequestDto.builder()
			.name(Name.of("hello"))
			.build();

		// when
		memberService.update(updateMemberRequest, member);

		// then
		Member updatedMember = memberRepository.findById(memberId).orElseThrow();
		assertThat(updatedMember.getName()).isEqualTo(updateMemberRequest.getName());
		assertThat(updatedMember.getProfile()).isEqualTo(member.getProfile());
	}

	@Test
	@DisplayName("update 요청시 Member의 profile만 요청한 경우 profile만 수정된다")
	void shouldUpdateProfileWhenCallUpdateTest() {
		// given
		final Id memberId = Id.generateNextId();
		Member member = MemberTestHelper.generateMember(memberId);
		memberRepository.save(member);
		UpdateMemberRequestDto updateMemberRequest = UpdateMemberRequestDto.builder()
			.profile(new Image("profile"))
			.build();

		// when
		memberService.update(updateMemberRequest, member);

		// then
		Member updatedMember = memberRepository.findById(memberId).orElseThrow();
		assertThat(updatedMember.getName()).isEqualTo(member.getName());
		assertThat(updatedMember.getProfile()).isEqualTo(updateMemberRequest.getProfile());
	}

	@Test
	@DisplayName("update 요청시 모두 변경을 요청한경우 모두 수정된다")
	void shouldUpdateAllWhenCallUpdateTest() {
		// given
		final Id memberId = Id.generateNextId();
		Member member = MemberTestHelper.generateMember(memberId);
		member = memberRepository.save(member);
		UpdateMemberRequestDto updateMemberRequest = UpdateMemberRequestDto.builder()
			.name(Name.of("hello"))
			.profile(new Image("profile"))
			.build();

		// when
		memberService.update(updateMemberRequest, member);

		// then
		Member updatedMember = memberRepository.findById(memberId).orElseThrow();
		assertThat(updatedMember.getName()).isEqualTo(updateMemberRequest.getName());
		assertThat(updatedMember.getProfile()).isEqualTo(updateMemberRequest.getProfile());
	}
}