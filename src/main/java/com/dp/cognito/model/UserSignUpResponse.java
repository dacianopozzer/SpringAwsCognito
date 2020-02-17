package com.dp.cognito.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserSignUpResponse {
	
	private String username;
	private String userCreatedDate;
	private String lastModifiedDate;
	private boolean enabled;
	private String userStatus;
	private String password;
	private String email;
	private String partner;

	@Override
	public String toString() {
		return "UserSignUpResponse [username=" + username + ", userCreatedDate=" + userCreatedDate
				+ ", lastModifiedDate=" + lastModifiedDate + ", enabled=" + enabled + ", userStatus=" + userStatus
				+ ", email=" + email + ", partner=" + partner + "]";
	}

}
