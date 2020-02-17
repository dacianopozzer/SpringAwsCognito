package com.dp.cognito.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "com.ixortalk.security.jwt.aws")
public class JwtConfiguration {

	private static final String COGNITO_IDENTITY_POOL_URL = "https://cognito-idp.%s.amazonaws.com/%s";
	private static final String JSON_WEB_TOKEN_SET_URL_SUFFIX = "/.well-known/jwks.json";

	@Value("${aws.cognito.clientId}")
	private String clientId;

	@Value("${aws.cognito.poolId}")
	private String poolId;

	@Value("${aws.cognito.endpoint}")
	private String endpoint;

	@Value("${aws.cognito.region}")
	private String region;

	@Value("${aws.cognito.identityPoolId}")
	private String identityPoolId;

	@Value("${aws.cognito.developerGroup}")
	private String developerGroup;

	@Value("${aws.cognito.connectionTimeout}")
	private int connectionTimeout;

	@Value("${aws.cognito.readTimeout}")
	private int readTimeout;

	@Value("${aws.cognito.httpHeader}")
	private String httpHeader;

	private String userNameField = "cognito:username";
	private String groupsField = "cognito:groups";

	public String getJwkUrl() {
		StringBuilder cognitoURL = new StringBuilder();
		cognitoURL.append(COGNITO_IDENTITY_POOL_URL);
		cognitoURL.append(JSON_WEB_TOKEN_SET_URL_SUFFIX);
		return String.format(cognitoURL.toString(), region, poolId);
	}

	public String getCognitoIdentityPoolUrl() {
		return String.format(COGNITO_IDENTITY_POOL_URL, region, poolId);
	}

}
