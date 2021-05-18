package com.csye7125.notifier.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AwsSesConfiguration {

    private final String region;
    private final String access_key;
	private final String secret_key;

    public AwsSesConfiguration(@Value("${email.region}") String region,@Value("${aws.access-key}") String access_key , @Value("${aws.secret-key}") String secret_key)
	{
        this.region = region;
        this.access_key=access_key;
        this.secret_key=secret_key;

    }

    /**
     * Build the AWS ses client
     *
     * @return AmazonSimpleEmailServiceClientBuilder
     */
    @Bean
    public AmazonSimpleEmailService amazonSimpleEmailService() {
		return AmazonSimpleEmailServiceClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(access_key,secret_key )))
				.withRegion(region).build();
    }
}
