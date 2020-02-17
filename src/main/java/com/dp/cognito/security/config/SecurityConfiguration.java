package com.dp.cognito.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.dp.cognito.security.filter.AwsCognitoJwtAuthenticationFilter;
import com.dp.cognito.security.filter.CustomAuthenticationEntryPoint;
import com.dp.cognito.security.filter.RestAccessDeniedHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAuthenticationProvider authProvider;
    @Autowired
	private AwsCognitoJwtAuthenticationFilter awsCognitoJwtAuthenticationFilter;

   
    public SecurityConfiguration() {
        /*
         * Ignores the default configuration, useless in our case (session management, etc..)
         */
        super(true);
    }

    /* (non-Javadoc)
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#configure(org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder)
     */
    @Override
    protected void configure(
      AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider).eraseCredentials(false);
    }
 
 
    /* (non-Javadoc)
     * @see org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter#authenticationManagerBean()
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        /*
          Overloaded to expose Authenticationmanager's bean created by configure(AuthenticationManagerBuilder).
           This bean is used by the AuthenticationController.
         */
        return super.authenticationManagerBean();
    }
    
    

    @Override
    public void configure(WebSecurity web) throws Exception {
        // TokenAuthenticationFilter will ignore the below paths
    	web.ignoring().antMatchers("/auth");
        web.ignoring().antMatchers("/auth/**");
        web.ignoring().antMatchers("/v2/api-docs");
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
      
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        /* the secret key used to signe the JWT token is known exclusively by the server.
         With Nimbus JOSE implementation, it must be at least 256 characters longs.
         */
    	//In case we need to load the secret.key
        httpSecurity
	        /*
	         Forces all of the requests to require secure connection and redirect to those via the request handler and the https enabling flags added to application.properties
	         */
			//.requiresChannel().anyRequest().requiresSecure()
	        /*
	        Filters are added just after the ExceptionTranslationFilter so that Exceptions are catch by the exceptionHandling()
	         Further information about the order of filters, see FilterComparator
	         */
	       // .addFilterAfter(jwtTokenAuthenticationFilter("/**", secret), ExceptionTranslationFilter.class)
			//.and()
//	        .addFilterAfter(corsFilter(), ExceptionTranslationFilter.class)
	        /*
	         Exception management is handled by the authenticationEntryPoint (for exceptions related to authentications)
	         and by the AccessDeniedHandler (for exceptions related to access rights)
	        */
	        .exceptionHandling()
	        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
	        .accessDeniedHandler(new RestAccessDeniedHandler())
	        .and()
	        /*
	          anonymous() consider no authentication as being anonymous instead of null in the security context.
	         */
	        .anonymous()
	        .and()
	        /* No Http session is used to get the security context */
	        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        .and()
	        .authorizeRequests()
	            /* All access to the authentication service are permitted without authentication (actually as anonymous) */
	        .antMatchers("/auth").permitAll()
	        .antMatchers("/sample").hasAnyRole("ADMIN", "GIFTLIST_API")
	            /* All the other requests need an authentication.
	             Role access is done on Methods using annotations like @PreAuthorize
	             */
	        .anyRequest().authenticated()
	        .and()
			.addFilterBefore(awsCognitoJwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
