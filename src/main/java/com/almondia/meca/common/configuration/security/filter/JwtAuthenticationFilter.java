package com.almondia.meca.common.configuration.security.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.almondia.meca.auth.jwt.service.JwtTokenService;
import com.almondia.meca.common.domain.vo.Id;
import com.almondia.meca.member.domain.entity.Member;
import com.almondia.meca.member.service.MemberService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String AUTHORIZATION_HEADER = "Authorization";
	private final JwtTokenService jwtTokenService;
	private final MemberService memberService;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain) throws ServletException, IOException {
		try {
			String accessToken = parseJwtAccessToken(request);
			if (accessToken != null && !jwtTokenService.isValidToken(accessToken)) {
				makeErrorResponse(response);
				return;
			}
			if (accessToken != null && jwtTokenService.isValidToken(accessToken)) {
				String memberId = jwtTokenService.getIdFromToken(accessToken);
				Member member = memberService.findMember(new Id(memberId));
				SecurityContextHolder.getContext().setAuthentication(makeToken(member));
			}

			filterChain.doFilter(request, response);
		} catch (IllegalArgumentException illegalArgumentException) {
			makeErrorResponse(response);
		}
	}

	private void makeErrorResponse(HttpServletResponse response) throws IOException {
		SecurityContextHolder.clearContext();
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.sendError(HttpStatus.UNAUTHORIZED.value(), "invalid access token");
	}

	private UsernamePasswordAuthenticationToken makeToken(Member member) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority(member.getRole().getDetails()));
		return new UsernamePasswordAuthenticationToken(member, "",
			authorities);
	}

	private String parseJwtAccessToken(HttpServletRequest request) {
		String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			return authorizationHeader.substring(7);
		}
		return null;
	}
}
