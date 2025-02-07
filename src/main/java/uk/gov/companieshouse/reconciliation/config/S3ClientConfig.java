package uk.gov.companieshouse.reconciliation.config;

import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.sts.StsClient;
import software.amazon.awssdk.services.sts.auth.StsGetSessionTokenCredentialsProvider;

@Configuration
public class S3ClientConfig {

    @Bean
    public S3Client s3Client(CamelContext camelContext) {
        final var stsClient = StsClient.builder()
                .region(Region.of("eu-west-2"))
                .build();
        final var stsGetSessionTokenCredentialsProvider =
                StsGetSessionTokenCredentialsProvider.builder().
                        stsClient(stsClient)
                        .build();
        S3Client s3Client = S3Client.builder().
                region(Region.of("eu-west-2")).
                credentialsProvider(stsGetSessionTokenCredentialsProvider)
                .build();
        camelContext.getRegistry().bind("s3Client", s3Client);
        return s3Client;
    }
}