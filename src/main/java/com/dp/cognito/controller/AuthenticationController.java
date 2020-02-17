package com.dp.cognito.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dp.cognito.model.AuthenticationRequest;
import com.dp.cognito.model.AuthenticationResponse;
import com.dp.cognito.model.UserResponse;
import com.dp.cognito.services.AwsCognitoService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	@Autowired(required = false)
	private AuthenticationManager authenticationManager;

	@Autowired(required = false)
	private AwsCognitoService authService;	
	

	@SuppressWarnings("unchecked")
	@CrossOrigin
	@PostMapping
	public ResponseEntity<AuthenticationResponse> authentication(@RequestBody AuthenticationRequest authenticationRequest){

		String expiresIn = null;
		String token = null;
		String sessionToken = null;
		String refreshToken = null;
		String username = authenticationRequest.getUsername();
		String password = authenticationRequest.getPassword();
		String newPassword = authenticationRequest.getNewPassword();
		AuthenticationResponse authenticationResponse = null;

		Map <String, String> credentials = new HashMap<>();
		credentials.put("password", password);
		credentials.put("newPassword", newPassword);


		Authentication authentication = this.authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(username, credentials));
		
		Map<String,String> authenticatedCredentials = (Map<String, String>) authentication.getCredentials();
		token = authenticatedCredentials.get("idToken");
		expiresIn = authenticatedCredentials.get("expiresIn");
		sessionToken = authenticatedCredentials.get("accessToken");
		refreshToken = authenticatedCredentials.get("refreshToken");
		

		UserResponse userResponse = authService.getUserInfo(sessionToken);
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		
		authenticationResponse = new AuthenticationResponse(token, expiresIn, sessionToken, refreshToken, userResponse);
		authenticationResponse.setAccessToken(token);
		authenticationResponse.setExpiresIn(expiresIn);
		authenticationResponse.setSessionToken(sessionToken);
		authenticationResponse.setRefreshToken(refreshToken);
		authenticationResponse.setUserData(userResponse);		

		// Return the token
		return ResponseEntity.ok(new AuthenticationResponse(token, expiresIn, sessionToken, refreshToken, userResponse));

	}
	
}
