package uk.gov.companieshouse.reconciliation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.ses.SesClient;

import java.net.URI;

@Configuration
public class Config {

    @Bean("testS3Client")
    S3Client s3Client(@Value("${s3.endpoint.override}") String s3EndpointOverride,
                      @Value("${aws.access.key}") String awsAccessKey,
                      @Value("${aws.secret.key}") String awsSecretKey,
                      @Value("${aws.region}") String awsRegion) {
        return S3Client.builder()
                .endpointOverride(URI.create(s3EndpointOverride))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
                .region(Region.of(awsRegion))
                .build();
    }

    @Bean("testPresigner")
    S3Presigner s3Presigner(@Value("${s3.endpoint.override}") String s3EndpointOverride,
                            @Value("${aws.access.key}") String awsAccessKey,
                            @Value("${aws.secret.key}") String awsSecretKey,
                            @Value("${aws.region}") String awsRegion) {
        return S3Presigner.builder()
                .endpointOverride(URI.create(s3EndpointOverride))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
                .region(Region.of(awsRegion))
                .build();
    }

    @Bean("testSesClient")
    SesClient sesClient(@Value("${ses.endpoint.override}") String sesEndpointOverride,
                        @Value("${aws.access.key}") String awsAccessKey,
                        @Value("${aws.secret.key}") String awsSecretKey,
                        @Value("${aws.region}") String awsRegion) {
        return SesClient.builder()
                .endpointOverride(URI.create(sesEndpointOverride))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(awsAccessKey, awsSecretKey)))
                .region(Region.of(awsRegion))
                .build();
    }
}
