package com.dp.cognito.security.filter;

import static java.util.Collections.singletonMap;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.dp.cognito.security.model.ErrorMessage;
import com.dp.cognito.security.model.ResponseWrapper;
import com.dp.cognito.security.model.RestErrorList;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        RestErrorList errorList = new RestErrorList(SC_UNAUTHORIZED, new ErrorMessage(authException.getMessage()));
        ResponseWrapper responseWrapper = new ResponseWrapper(null, singletonMap("status", SC_UNAUTHORIZED), errorList);
        ObjectMapper objMapper = new ObjectMapper();

        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response);
        wrapper.setStatus(SC_UNAUTHORIZED);
        wrapper.setContentType(APPLICATION_JSON_VALUE);
        wrapper.getWriter().println(objMapper.writeValueAsString(responseWrapper));
        wrapper.getWriter().flush();
    }
}
