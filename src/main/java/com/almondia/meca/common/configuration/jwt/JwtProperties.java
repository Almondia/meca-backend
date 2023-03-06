package com.almondia.meca.common.configuration.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "jwt")
@NoArgsConstructor
@Setter
@Getter
public class JwtProperties {

	private String secretKey;
	private int expirationMs;
}

