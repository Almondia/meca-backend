package com.almondia.meca.common.domain.vo;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Embeddable;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;
import com.github.f4b6a3.ulid.Ulid;
import com.github.f4b6a3.ulid.UlidCreator;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Id implements Serializable, Wrapper, Comparable<Id> {

	private static final long serialVersionUID = -2772995063676474658L;

	private UUID uuid;

	public Id(UUID uuid) {
		this.uuid = uuid;
	}

	public Id(String uuid) {
		this.uuid = UUID.fromString(uuid);
	}

	public static Id generateNextId() {
		Ulid ulid = UlidCreator.getMonotonicUlid();
		return new Id(ulid.toUuid());
	}

	@Override
	public String toString() {
		return uuid.toString();
	}

	@Override
	public int compareTo(Id o) {
		return this.uuid.compareTo(o.uuid);
	}
}
