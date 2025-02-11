package uk.gov.companieshouse.reconciliation.config.aws;

import org.apache.camel.CamelContext;
import org.apache.camel.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3ClientConfig {

    @Value("${aws.region}")
    private String awsRegion;

    @Bean
    public S3Client s3Client(CamelContext camelContext) {
        S3Client s3Client = S3Client.builder().region(Region.of(awsRegion))
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create()).build();
        camelContext.getRegistry().bind("s3Client", s3Client);
        return s3Client;
    }
}
