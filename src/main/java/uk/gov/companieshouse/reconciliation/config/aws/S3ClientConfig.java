package uk.gov.companieshouse.reconciliation.config.aws;

import org.apache.camel.CamelContext;
import org.apache.camel.Configuration;
import org.springframework.context.annotation.Bean;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkSystemSetting;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3ClientConfig {

    @Bean("createS3Client")
    public S3Client createS3Client(CamelContext camelContext) {
        S3Client s3Client = S3Client.builder()
                .region(Region.of(System.getenv(SdkSystemSetting.AWS_REGION.environmentVariable())))
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create()).build();
        camelContext.getRegistry().bind("createS3Client", s3Client);
        return s3Client;
    }

}
