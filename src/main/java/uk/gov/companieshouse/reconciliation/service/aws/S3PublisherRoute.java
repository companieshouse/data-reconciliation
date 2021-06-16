package uk.gov.companieshouse.reconciliation.service.aws;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequest;

/**
 * Publishes {@link PublisherResourceRequest resource request} by uploading CSV files to S3.
 * Assigns a pre-signed url for each request and finally puts the url into a ResourceLinkReference header.
 */
@Component
public class S3PublisherRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:s3-publisher")
                .toD("${header.Upload}")
                .toD("${header.Presign}")
                .setHeader("ResourceLinkReference", body());
    }
}
