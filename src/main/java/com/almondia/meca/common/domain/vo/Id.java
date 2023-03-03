package com.almondia.meca.common.domain.vo;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Embeddable;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Id implements Serializable, Wrapper {

	private static final long serialVersionUID = -2772995063676474658L;

	private UUID uuid;

	public Id(UUID uuid) {
		this.uuid = uuid;
	}

	public Id(String uuid) {
		this.uuid = UUID.fromString(uuid);
	}

	public static Id generateNextId() {
		UUID uuid = UUID.randomUUID();
		return new Id(uuid);
	}

	@Override
	public String toString() {
		return uuid.toString();
	}
}
