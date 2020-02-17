package com.dp.cognito.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordRequest {

	private String username;
	private String password;
	private String confirmationCode;
	private String oldPassword;
	private String accessToken;

}
