package com.dp.cognito.security.model;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import static java.util.Arrays.asList;

@Getter
@Setter
public class RestErrorList extends ArrayList<ErrorMessage> {

	private static final long serialVersionUID = 1L;

	private HttpStatus status;

	public RestErrorList(HttpStatus status, ErrorMessage... errors) {
		this(status.value(), errors);
	}

	public RestErrorList(int status, ErrorMessage... errors) {
		super();
		this.status = HttpStatus.valueOf(status);
		addAll(asList(errors));
	}

}