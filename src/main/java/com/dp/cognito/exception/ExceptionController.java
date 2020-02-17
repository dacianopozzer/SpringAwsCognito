package com.dp.cognito.exception;

import static java.util.Collections.singletonMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.dp.cognito.exception.CognitoException;
import com.dp.cognito.security.model.ErrorMessage;
import com.dp.cognito.security.model.ResponseWrapper;
import com.dp.cognito.security.model.RestErrorList;

@ControllerAdvice
@EnableWebMvc
public class ExceptionController extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public @ResponseBody ResponseEntity<ResponseWrapper> handleException(HttpServletRequest request, ResponseWrapper responseWrapper) {

		return ResponseEntity.ok(responseWrapper);
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ResponseWrapper> handleIOException(HttpServletRequest request, CognitoException e) {

		RestErrorList errorList = new RestErrorList(HttpStatus.NOT_ACCEPTABLE, new ErrorMessage(e.getErrorMessage(), e.getErrorCode(), e.getDetailErrorMessage()));
		ResponseWrapper responseWrapper = new ResponseWrapper(singletonMap("status", HttpStatus.NOT_ACCEPTABLE), null, errorList);

		return ResponseEntity.ok(responseWrapper);
	}

	public ResponseWrapper handleJwtException(HttpServletRequest request, CognitoException e) {

		RestErrorList errorList = new RestErrorList(HttpStatus.UNAUTHORIZED, new ErrorMessage(e.getErrorMessage(), e.getErrorCode(), e.getDetailErrorMessage()));
		ResponseWrapper responseWrapper = new ResponseWrapper(singletonMap("status", HttpStatus.UNAUTHORIZED), null, errorList);

		return responseWrapper;
	}

}