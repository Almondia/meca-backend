package com.almondia.meca.auth.jwt.service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import com.almondia.meca.common.configuration.jwt.JwtProperties;
import com.almondia.meca.common.domain.vo.Id;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtTokenService {

	private static final String ID_CLAIM_KEY = "id";
	private final JwtProperties jwtProperties;

	public String createToken(Id id) {
		Instant now = Instant.now();
		Instant expirationDate = now.plusMillis(jwtProperties.getExpirationMs());
		Claims claims = Jwts.claims();
		claims.put(ID_CLAIM_KEY, id.toString());
		return Jwts.builder()
			.setSubject(id.toString())
			.setIssuedAt(Date.from(now))
			.setClaims(claims)
			.setExpiration(Date.from(expirationDate))
			.signWith(getSigningKey(), SignatureAlgorithm.HS512)
			.compact();
	}

	public String getIdFromToken(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(getSigningKey())
			.build()
			.parseClaimsJws(token)
			.getBody()
			.get(ID_CLAIM_KEY, String.class);
	}

	public boolean isValidToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
			return true;
		} catch (JwtException | IllegalArgumentException | MalformedJwtException | ExpiredJwtException e) {
			return false;
		}
	}

	private byte[] getSecretKeyBytes() {
		return Decoders.BASE64.decode(jwtProperties.getSecretKey());
	}

	private Key getSigningKey() {
		return Keys.hmacShaKeyFor(getSecretKeyBytes());
	}
}
