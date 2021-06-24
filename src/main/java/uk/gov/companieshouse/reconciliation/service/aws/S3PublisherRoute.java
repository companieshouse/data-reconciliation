package uk.gov.companieshouse.reconciliation.service.aws;

import org.apache.camel.LoggingLevel;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import uk.gov.companieshouse.reconciliation.common.RetryableRoute;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequest;

/**
 * Publishes {@link PublisherResourceRequest resource request} by uploading CSV files to S3.
 * Assigns a pre-signed download link for each request than puts that url into a ResourceLinkReference header.
 */
@Component
public class S3PublisherRoute extends RetryableRoute {

    @Override
    public void configure() {
        super.configure();
        from("direct:s3-publisher")
                .onException(AwsServiceException.class)
                    .handled(true)
                    .log(LoggingLevel.ERROR, "Failed to publish results to S3.")
                    .setHeader("Failed").constant(true)
                .end()
                .toD("${header.Upload}")
                .toD("${header.Presign}")
                .setHeader("ResourceLinkReference", body())
                .end();
    }
}
