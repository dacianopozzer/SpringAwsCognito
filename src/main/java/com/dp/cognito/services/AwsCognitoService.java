package com.dp.cognito.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.AdminInitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeRequest;
import com.amazonaws.services.cognitoidp.model.AdminRespondToAuthChallengeResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.ChallengeNameType;
import com.amazonaws.services.cognitoidp.model.GetUserRequest;
import com.amazonaws.services.cognitoidp.model.GetUserResult;
import com.dp.cognito.exception.CognitoException;
import com.dp.cognito.model.AuthenticationRequest;
import com.dp.cognito.model.SpringSecurityUserDetails;
import com.dp.cognito.model.UserResponse;
import com.dp.cognito.security.config.AwsClientProviderBuilder;
import com.dp.cognito.security.config.JwtConfiguration;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AwsCognitoService {

	private static final String PASS_WORD = "PASSWORD";
	private static final String USERNAME = "USERNAME";
	private static final String NEW_PASS_WORD = "NEW_PASSWORD";
	private static final String NEW_PASS_WORD_REQUIRED = "NEW_PASSWORD_REQUIRED";
	
	@Autowired
	AwsClientProviderBuilder cognitoBuilder;

	@Autowired
	private JwtConfiguration cognitoConfig;

	private AWSCognitoIdentityProvider getAmazonCognitoIdentityClient() {

		return cognitoBuilder.getAWSCognitoIdentityClient();

	}

	public SpringSecurityUserDetails authenticate(AuthenticationRequest authenticationRequest) {

		AuthenticationResultType authenticationResult = null;
		AWSCognitoIdentityProvider cognitoClient = getAmazonCognitoIdentityClient();

		try {

			final Map<String, String> authParams = new HashMap<>();
			authParams.put(USERNAME, authenticationRequest.getUsername());
			authParams.put(PASS_WORD, authenticationRequest.getPassword());

			final AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest();
			authRequest.withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
						.withClientId(cognitoConfig.getClientId())
						.withUserPoolId(cognitoConfig.getPoolId())
						.withAuthParameters(authParams);

			AdminInitiateAuthResult result = cognitoClient.adminInitiateAuth(authRequest);

			if (!StringUtils.isEmpty(result.getChallengeName())) {

				if (NEW_PASS_WORD_REQUIRED.equals(result.getChallengeName())) {

					if (null == authenticationRequest.getNewPassword()) {
						throw new CognitoException("User must provide a new password",
								CognitoException.USER_MUST_CHANGE_PASS_WORD_EXCEPTION_CODE, result.getChallengeName());
					} else {
						final Map<String, String> challengeResponses = new HashMap<>();
						challengeResponses.put(USERNAME, authenticationRequest.getUsername());
						challengeResponses.put(PASS_WORD, authenticationRequest.getPassword());
						challengeResponses.put(NEW_PASS_WORD, authenticationRequest.getNewPassword());

						final AdminRespondToAuthChallengeRequest request = new AdminRespondToAuthChallengeRequest();
						request.withChallengeName(ChallengeNameType.NEW_PASSWORD_REQUIRED)
								.withChallengeResponses(challengeResponses).withClientId(cognitoConfig.getClientId())
								.withUserPoolId(cognitoConfig.getPoolId()).withSession(result.getSession());

						AdminRespondToAuthChallengeResult resultChallenge = cognitoClient
								.adminRespondToAuthChallenge(request);
						authenticationResult = resultChallenge.getAuthenticationResult();

					}
				} else {
					throw new CognitoException(result.getChallengeName(),
							CognitoException.USER_MUST_DO_ANOTHER_CHALLENGE, result.getChallengeName());
				}

			} else {
				authenticationResult = result.getAuthenticationResult();
			}

			SpringSecurityUserDetails userAuthenticated = new SpringSecurityUserDetails(authenticationRequest.getUsername(), authenticationRequest.getPassword(), null, null, null);
			userAuthenticated.setAccessToken(authenticationResult.getAccessToken());
			userAuthenticated.setExpiresIn(authenticationResult.getExpiresIn());
			userAuthenticated.setTokenType(authenticationResult.getTokenType());
			userAuthenticated.setRefreshToken(authenticationResult.getRefreshToken());
			userAuthenticated.setIdToken(authenticationResult.getIdToken());

			log.info("User successfully authenticated userInfo: username {}", authenticationRequest.getUsername());

			return userAuthenticated;
		} catch (com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException e) {
			log.error(e.getMessage(), e);
			throw new CognitoException(e.getMessage(), e.getErrorCode(), e.getMessage() + e.getErrorCode());
		} catch (CognitoException cognitoException) {
			throw cognitoException;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CognitoException(e.getMessage(), CognitoException.GENERIC_EXCEPTION_CODE, e.getMessage());
		}

	}

	public UserResponse getUserInfo(String accessToken) {
		AWSCognitoIdentityProvider cognitoClient = getAmazonCognitoIdentityClient();

		try {

			if (StringUtils.isEmpty(accessToken)) {
				throw new CognitoException("User must provide an access token",
						CognitoException.INVALID_ACCESS_TOKEN_EXCEPTION, "User must provide an access token");
			}

			GetUserRequest userRequest = new GetUserRequest().withAccessToken(accessToken);

			GetUserResult userResult = cognitoClient.getUser(userRequest);

			List<AttributeType> userAttributes = userResult.getUserAttributes();
			UserResponse userResponse = getUserAttributesData(userAttributes, userResult.getUsername());

			return userResponse;

		} catch (com.amazonaws.services.cognitoidp.model.AWSCognitoIdentityProviderException e) {
			log.error(e.getMessage(), e);
			throw new CognitoException(e.getMessage(), e.getErrorCode(), e.getMessage() + e.getErrorCode());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new CognitoException(e.getMessage(), CognitoException.GENERIC_EXCEPTION_CODE, e.getMessage());
		}

	}

	private UserResponse getUserAttributesData(List<AttributeType> userAttributes, String username) {
		UserResponse userResponse = new UserResponse();

		userResponse.setUsername(username);

		for (AttributeType attribute : userAttributes) {
			if (attribute.getName().equals("email")) {
				userResponse.setEmail(attribute.getValue());
			} else if (attribute.getName().equals("phone_number")) {
				userResponse.setPhoneNumber(attribute.getValue());
			} else if (attribute.getName().equals("name")) {
				userResponse.setName(attribute.getValue());
			} else if (attribute.getName().equals("family_name")) {
				userResponse.setLastname(attribute.getValue());
			} else if (attribute.getName().equals("custom:partner")) {
				userResponse.setPartner(attribute.getValue());
			}
		}

		return userResponse;
	}

}
