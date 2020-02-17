package com.dp.cognito;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.RegistrationBean;
import org.springframework.context.annotation.Bean;

import com.dp.cognito.security.filter.AwsCognitoJwtAuthenticationFilter;


@SpringBootApplication
public class SpringAwsCognito {
	
	public static void main(String[] args) {
		SpringApplication.run(SpringAwsCognito.class, args);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Bean
	public RegistrationBean jwtAuthFilterRegister(AwsCognitoJwtAuthenticationFilter filter) {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean(filter);
		registrationBean.setEnabled(false);
		return registrationBean;
	}	
	
}
