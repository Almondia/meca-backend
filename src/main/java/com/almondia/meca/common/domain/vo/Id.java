package com.almondia.meca.common.domain.vo;

import java.io.Serializable;

import javax.persistence.Embeddable;

import com.almondia.meca.common.configuration.jackson.module.wrapper.Wrapper;
import com.github.f4b6a3.tsid.Tsid;
import com.github.f4b6a3.tsid.TsidCreator;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Id implements Serializable, Wrapper, Comparable<Id> {

	private static final long serialVersionUID = -2772995063676474658L;

	private Tsid tsid;

	public Id(Tsid tsid) {
		this.tsid = tsid;
	}

	public Id(String tsid) {
		this.tsid = Tsid.from(tsid);
	}

	public static Id generateNextId() {
		Tsid tsid = TsidCreator.getTsid();
		return new Id(tsid);
	}

	@Override
	public String toString() {
		return tsid.toString();
	}

	@Override
	public int compareTo(Id o) {
		return this.tsid.compareTo(o.tsid);
	}
}
