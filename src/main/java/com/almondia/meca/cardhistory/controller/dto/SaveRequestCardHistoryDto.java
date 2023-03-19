package com.almondia.meca.cardhistory.controller.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
public class SaveRequestCardHistoryDto {

	private List<CardHistoryDto> cardHistoryDtos;

}
