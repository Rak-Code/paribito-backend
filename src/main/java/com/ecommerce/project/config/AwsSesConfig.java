package com.ecommerce.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

/**
 * Configuration class for AWS SES (Simple Email Service).
 * Creates SesClient bean configured with IAM credentials.
 * Only loaded when email.provider=aws-ses.
 */
@Configuration
@ConditionalOnProperty(name = "email.provider", havingValue = "aws-ses", matchIfMissing = true)
public class AwsSesConfig {

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;

    @Value("${aws.region:ap-south-1}")
    private String region;

    /**
     * Creates AWS SES client bean with IAM credentials.
     * Configured for the specified AWS region (default: ap-south-1 Mumbai).
     *
     * @return SesClient configured with credentials and region
     */
    @Bean
    public SesClient sesClient() {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey);
        
        return SesClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();
    }
}
