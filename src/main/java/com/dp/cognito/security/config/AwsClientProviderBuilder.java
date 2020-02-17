package com.dp.cognito.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AwsClientProviderBuilder {

	private AWSCognitoIdentityProvider cognitoIdentityProvider;
	private ClasspathPropertiesFileCredentialsProvider propertiesFileCredentialsProvider;

	@Value("${aws.cognito.region}")
	private String region;

	private void initCommonInfo() {
		if (null == propertiesFileCredentialsProvider) {
			propertiesFileCredentialsProvider = new ClasspathPropertiesFileCredentialsProvider();
		}
	}

	public AWSCognitoIdentityProvider getAWSCognitoIdentityClient() {
		if (null == cognitoIdentityProvider) {
			initCommonInfo();

			cognitoIdentityProvider = AWSCognitoIdentityProviderClientBuilder.standard()
					.withCredentials(propertiesFileCredentialsProvider).withRegion(region).build();
		}

		return cognitoIdentityProvider;
	}

}
