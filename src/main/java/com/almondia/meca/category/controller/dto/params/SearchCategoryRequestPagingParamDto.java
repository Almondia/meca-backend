package com.almondia.meca.category.controller.dto.params;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Getter
public class SearchCategoryRequestPagingParamDto {
	private int offset;
	private int pageSize;
	private String sortOption;
	private String sortOrder;
	private String startsWithTitle;
	private String startCreatedAt;
	private String endCreatedAt;
	private String startModifiedAt;
	private String endModifiedAt;
	private String eqMemberId;
	private boolean eqShared;
	private boolean eqDeleted;
}
