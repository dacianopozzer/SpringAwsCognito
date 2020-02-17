package com.dp.cognito.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpRequest {

	private String username;
	private String password;
	private String email;
	private String partner;
	private String name;
	private String lastname;
	private String phoneNumber;
	private String agreementFlag;

	@Override
	public String toString() {
		return "UserSignUpRequest [username=" + username + ", email=" + email + ", partner=" + partner + ", name="
				+ name + ", lastname=" + lastname + ", phoneNumber=" + phoneNumber + "]";
	}

}
