package com.dp.cognito.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequest {

	private String username;
	private String password;
	private String newPassword;
	private String accessToken;
	private String refreshToken;

}