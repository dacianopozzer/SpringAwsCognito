package com.dp.cognito.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {

	private String username;
	private String partner;
	private String email;
	private String userCreateDate;
	private String userStatus;
	private String lastModifiedDate;
	private String name;
	private String lastname;
	private String phoneNumber;
	private String message;

}
