package com.dp.cognito.security.config;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;

public class JwtAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) {
		return authentication;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return true;
	}
}
