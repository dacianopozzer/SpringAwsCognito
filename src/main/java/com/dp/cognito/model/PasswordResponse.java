package com.dp.cognito.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordResponse {

	private String destination;
	private String deliveryMedium;
	private String message;
	private String username;

}
