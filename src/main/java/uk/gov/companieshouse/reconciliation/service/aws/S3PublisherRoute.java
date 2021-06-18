package uk.gov.companieshouse.reconciliation.service.aws;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequest;
import uk.gov.companieshouse.reconciliation.common.RetryableRoute;

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
                    .setHeader("Failed").constant(true)
                .end()
                .toD("${header.Upload}")
                .toD("${header.Presign}")
                .setHeader("ResourceLinkReference", body())
                .end();
    }
}
