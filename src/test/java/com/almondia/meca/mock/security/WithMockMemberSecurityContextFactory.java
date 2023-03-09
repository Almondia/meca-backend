package com.almondia.meca.mock.security;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.domain.vo.Email;
import com.almondia.meca.member.domain.vo.Name;

public class WithMockMemberSecurityContextFactory implements WithSecurityContextFactory<WithMockMember> {

	@Override
	public SecurityContext createSecurityContext(WithMockMember annotation) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		Member member = Member.builder()
			.name(new Name(annotation.name()))
			.email(new Email(annotation.email()))
			.memberId(new Id(annotation.id()))
			.createdAt(LocalDateTime.now())
			.modifiedAt(LocalDateTime.now())
			.oAuthType(annotation.oAuthType())
			.role(annotation.role())
			.build();
		Collection<? extends GrantedAuthority> authorities = Collections.singletonList(
			new SimpleGrantedAuthority(member.getRole().getDetails())
		);
		context.setAuthentication(new UsernamePasswordAuthenticationToken(member, "", authorities));
		return context;
	}
}
