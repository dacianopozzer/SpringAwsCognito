package com.dp.cognito.security.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.dp.cognito.model.AuthenticationRequest;
import com.dp.cognito.model.SpringSecurityUserDetails;
import com.dp.cognito.services.AwsCognitoService;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	AwsCognitoService cognitoService;

	/* (non-Javadoc)
	 * @see org.springframework.security.authentication.AuthenticationProvider#authenticate(org.springframework.security.core.Authentication)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Authentication authenticate(Authentication authentication) {
		AuthenticationRequest authenticationRequest = null;


		if(null != authentication) {
			authenticationRequest = new AuthenticationRequest();
			Map <String,String> credentials = (Map<String, String>) authentication.getCredentials();
			authenticationRequest.setNewPassword(credentials.get("newPassword"));
			authenticationRequest.setPassword(credentials.get("password"));
			authenticationRequest.setUsername(authentication.getName());
			
			
			SpringSecurityUserDetails userAuthenticated = cognitoService.authenticate(authenticationRequest);
			if (null != userAuthenticated) {
				
				Map <String, String> authenticatedCredentials = new HashMap<>();
				authenticatedCredentials.put("accessToken", userAuthenticated.getAccessToken());
				authenticatedCredentials.put("expiresIn", userAuthenticated.getExpiresIn().toString());
				authenticatedCredentials.put("idToken", userAuthenticated.getIdToken());
				authenticatedCredentials.put("newPassword", userAuthenticated.getPassword());
				authenticatedCredentials.put("refreshToken", userAuthenticated.getRefreshToken());
				authenticatedCredentials.put("tokenType", userAuthenticated.getTokenType());
				return new UsernamePasswordAuthenticationToken(
						userAuthenticated.getUsername(), authenticatedCredentials, userAuthenticated.getAuthorities());
			} else {
				return null;
			}
		}else {
			throw new UsernameNotFoundException(String.format("No appUser found with username '%s'.", ""));
		}
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(
				UsernamePasswordAuthenticationToken.class);
	}
	
	
}

