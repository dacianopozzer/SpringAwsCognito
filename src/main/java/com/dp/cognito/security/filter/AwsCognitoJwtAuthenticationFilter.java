package com.dp.cognito.security.filter;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dp.cognito.exception.CognitoException;
import com.dp.cognito.exception.ExceptionController;
import com.dp.cognito.security.model.ResponseWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.proc.BadJOSEException;

public class AwsCognitoJwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String ERROR_OCCURED_WHILE_PROCESSING_THE_TOKEN = "Error occured while processing the token";
	private static final String INVALID_TOKEN_MESSAGE = "Invalid Token";

	private AwsCognitoIdTokenProcessor awsCognitoIdTokenProcessor;

	@Autowired
	private ApplicationContext appContext;

	public AwsCognitoJwtAuthenticationFilter(AwsCognitoIdTokenProcessor awsCognitoIdTokenProcessor) {
		this.awsCognitoIdTokenProcessor = awsCognitoIdTokenProcessor;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Authentication authentication = null;
		try {
			authentication = awsCognitoIdTokenProcessor.getAuthentication((HttpServletRequest) request);

			SecurityContextHolder.getContext().setAuthentication(authentication);

		} catch (BadJOSEException e) {
			SecurityContextHolder.clearContext();
			createExceptionResponse(request, response, new CognitoException(INVALID_TOKEN_MESSAGE, CognitoException.INVALID_TOKEN_EXCEPTION_CODE, e.getMessage()));
			return;
		} catch (CognitoException e) {
			SecurityContextHolder.clearContext();
			createExceptionResponse(request, response, new CognitoException(e.getErrorMessage(), CognitoException.INVALID_TOKEN_EXCEPTION_CODE, e.getDetailErrorMessage()));
			return;
		} catch (Exception e) {
			SecurityContextHolder.clearContext();
			createExceptionResponse(request, response, new CognitoException(ERROR_OCCURED_WHILE_PROCESSING_THE_TOKEN, CognitoException.INVALID_TOKEN_EXCEPTION_CODE, e.getMessage()));
			return;
		}

		filterChain.doFilter(request, response);

	}

	private void createExceptionResponse(ServletRequest request, ServletResponse response, CognitoException exception)
			throws IOException {
		HttpServletRequest req = (HttpServletRequest) request;
		ExceptionController exceptionController = null;
		ObjectMapper objMapper = new ObjectMapper();

		exceptionController = appContext.getBean(ExceptionController.class);
		ResponseWrapper responseWrapper = exceptionController.handleJwtException(req, exception);

		HttpServletResponse httpResponse = (HttpServletResponse) response;

		final HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(httpResponse);
		wrapper.setStatus(HttpStatus.UNAUTHORIZED.value());
		wrapper.setContentType(APPLICATION_JSON_VALUE);
		wrapper.getWriter().println(objMapper.writeValueAsString(responseWrapper));
		wrapper.getWriter().flush();

	}
}