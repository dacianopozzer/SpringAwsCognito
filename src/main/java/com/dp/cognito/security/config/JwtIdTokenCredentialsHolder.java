package com.dp.cognito.security.config;

public class JwtIdTokenCredentialsHolder {

	private String idToken;

	public String getIdToken() {
		return idToken;
	}

	public JwtIdTokenCredentialsHolder setIdToken(String idToken) {
		this.idToken = idToken;
		return this;
	}

}
