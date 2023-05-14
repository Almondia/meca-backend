package com.almondia.meca.cardhistory.controller.dto;

import java.util.List;

import com.almondia.meca.common.domain.vo.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Builder
@ToString
public class SaveRequestCardHistoryDto {

	private List<CardHistoryRequestDto> cardHistories;
	private Id categoryId;
}
