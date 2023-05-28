package com.almondia.meca.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.almondia.meca.member.application.MemberService;
import com.almondia.meca.member.controller.dto.MemberResponseDto;
import com.almondia.meca.member.controller.dto.UpdateMemberRequestDto;
import com.almondia.meca.member.domain.entity.Member;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/me")
	@Secured("ROLE_USER")
	public ResponseEntity<MemberResponseDto> findMyProfile(@AuthenticationPrincipal Member member) {
		MemberResponseDto myProfile = memberService.findMyProfile(member.getMemberId());
		return ResponseEntity.ok(myProfile);
	}

	@PutMapping("/me")
	@Secured("ROLE_USER")
	public ResponseEntity<MemberResponseDto> updateMyProfile(@AuthenticationPrincipal Member member,
		@RequestBody UpdateMemberRequestDto updateMemberRequestDto) {
		MemberResponseDto myProfile = memberService.update(updateMemberRequestDto, member);
		return ResponseEntity.ok(myProfile);
	}
}
