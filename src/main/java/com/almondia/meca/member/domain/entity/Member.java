package com.almondia.meca.member.domain.entity;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.almondia.meca.common.domain.entity.DateEntity;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.vo.Email;
import com.almondia.meca.member.domain.vo.Name;
import com.almondia.meca.member.domain.vo.OAuthType;
import com.almondia.meca.member.domain.vo.Role;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@SuperBuilder
public class Member extends DateEntity {

	@EmbeddedId
	@AttributeOverride(name = "uuid", column = @Column(name = "member_id", nullable = false, columnDefinition = "BINARY(16)"))
	private Id memberId;

	@Column(name = "oauth_id", nullable = false, unique = true)
	private String oauthId;

	@Embedded
	@AttributeOverride(name = "name", column = @Column(name = "name", nullable = false, columnDefinition = "VARCHAR(40)"))
	private Name name;

	@Embedded
	@AttributeOverride(name = "email", column = @Column(name = "email", columnDefinition = "VARCHAR(255)"))
	private Email email;

	@Column(name = "o_auth_type", nullable = false, columnDefinition = "VARCHAR(10)")
	private OAuthType oAuthType;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, columnDefinition = "VARCHAR(10)")
	private Role role;

	private boolean isDeleted;

	public void delete() {
		this.isDeleted = true;
	}

	public void restore() {
		this.isDeleted = false;
	}

	public boolean isDeleted() {
		return this.isDeleted;
	}
}