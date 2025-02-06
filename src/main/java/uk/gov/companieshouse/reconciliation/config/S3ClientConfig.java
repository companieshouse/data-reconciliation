package uk.gov.companieshouse.reconciliation.config;

import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3ClientConfig {
    @Value("${aws.access.key}")
    private String accessKey;
    @Value("${aws.secret.key}")
    private String secretKey;

    @Bean("S3Client")
    public S3Client s3Client(CamelContext camelContext) {
        AwsCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        S3Client s3Client = S3Client
                .builder()
                .region(Region.of("eu-west-2"))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
        camelContext.getRegistry().bind("s3Client", s3Client);
        return s3Client;
    }
}