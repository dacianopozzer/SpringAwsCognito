package com.dp.cognito.security.filter;

import java.text.ParseException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.dp.cognito.exception.CognitoException;
import com.dp.cognito.security.config.CognitoJwtAuthentication;
import com.dp.cognito.security.config.JwtConfiguration;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;

public class AwsCognitoIdTokenProcessor {

	private static final String ROLE_PREFIX = "ROLE_";
	private static final String EMPTY_STRING = "";
	private static final String BEARER_PREFIX = "Bearer ";

	private static final String INVALID_TOKEN = "Invalid Token";
	private static final String NO_TOKEN_FOUND = "Invalid Action, no token found";	
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private ConfigurableJWTProcessor configurableJWTProcessor;

	@Autowired
	private JwtConfiguration jwtConfiguration;

	@SuppressWarnings("unchecked")
	public Authentication getAuthentication(HttpServletRequest request)
			throws ParseException, BadJOSEException, JOSEException {
		String idToken = request.getHeader(jwtConfiguration.getHttpHeader());
		if (null == idToken) {
			throw new CognitoException(NO_TOKEN_FOUND, CognitoException.NO_TOKEN_PROVIDED_EXCEPTION,
					"No token found in Http Authorization Header");
		} else {

			idToken = stripBearerToken(idToken);
			JWTClaimsSet claimsSet = null;

			claimsSet = configurableJWTProcessor.process(idToken, null);

			if (!isIssuedCorrectly(claimsSet)) {
				throw new CognitoException(INVALID_TOKEN, CognitoException.INVALID_TOKEN_EXCEPTION_CODE,
						String.format("Issuer %s in JWT token doesn't match cognito idp %s", claimsSet.getIssuer(),
								jwtConfiguration.getCognitoIdentityPoolUrl()));
			}

			if (!isIdToken(claimsSet)) {
				throw new CognitoException(INVALID_TOKEN, CognitoException.NOT_A_TOKEN_EXCEPTION,
						"JWT Token doesn't seem to be an ID Token");
			}

			String username = claimsSet.getClaims().get(jwtConfiguration.getUserNameField()).toString();

			List<String> groups = (List<String>) claimsSet.getClaims().get(jwtConfiguration.getGroupsField());
			List<GrantedAuthority> grantedAuthorities = convertList(groups,
					group -> new SimpleGrantedAuthority(ROLE_PREFIX + group.toUpperCase()));
			User user = new User(username, EMPTY_STRING, grantedAuthorities);

			return new CognitoJwtAuthentication(user, claimsSet, grantedAuthorities);

		}

	}

	private String stripBearerToken(String token) {
		return token.startsWith(BEARER_PREFIX) ? token.substring(BEARER_PREFIX.length()) : token;
	}

	private boolean isIssuedCorrectly(JWTClaimsSet claimsSet) {
		return claimsSet.getIssuer().equals(jwtConfiguration.getCognitoIdentityPoolUrl());
	}

	private boolean isIdToken(JWTClaimsSet claimsSet) {
		return claimsSet.getClaim("token_use").equals("id");
	}

	private static <T, U> List<U> convertList(List<T> from, Function<T, U> func) {
		return from.stream().map(func).collect(Collectors.toList());
	}
}