package com.almondia.meca.category.infra.querydsl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CategorySearchOption {
	private String containTitle;
}
