package com.dp.cognito.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenticationResponse {

	private String accessToken;
	private String sessionToken;
	private String refreshToken;
	private String expiresIn;

	private String actualDate;
	private String expirationDate;
	private UserResponse userData;
	private String username;
	private String message;

	public AuthenticationResponse() {
		super();
	}

	public AuthenticationResponse(String accessToken, String expiresIn, String sessionToken, String refreshToken, UserResponse userData) {
		super();
		this.accessToken = accessToken;
		this.expiresIn = expiresIn;
		this.sessionToken = sessionToken;
		this.refreshToken = refreshToken;
		this.userData = userData;
	}

}