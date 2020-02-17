package com.dp.cognito.security.filter;

import static java.util.Collections.singletonMap;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.dp.cognito.security.model.ErrorMessage;
import com.dp.cognito.security.model.ResponseWrapper;
import com.dp.cognito.security.model.RestErrorList;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        RestErrorList errorList = new RestErrorList(403, new ErrorMessage(accessDeniedException.getMessage()));
        ResponseWrapper responseWrapper = new ResponseWrapper(null, singletonMap("status", 403), errorList);
        ObjectMapper objMapper = new ObjectMapper();

        final HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response);
        wrapper.setStatus(403);
        wrapper.setContentType(APPLICATION_JSON_VALUE);
        wrapper.getWriter().println(objMapper.writeValueAsString(responseWrapper));
        wrapper.getWriter().flush();
    }
}
