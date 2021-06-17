package uk.gov.companieshouse.reconciliation.function.email;

import org.apache.camel.Exchange;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;

/**
 * Serialises requests to upload results to S3
 */
public class EmailPublisherMapper {

    public void map(PublisherResourceRequest request, Exchange targetExchange) {
        targetExchange.getIn().setHeader(AWS2S3Constants.KEY, request.getObjectKey());
        targetExchange.getIn().setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, request.getExpirationTimeInMillis());
        targetExchange.getIn().setHeader("Upload", request.getUploaderEndpoint());
        targetExchange.getIn().setHeader("Presign", request.getPresignerEndpoint());
        targetExchange.getIn().setHeader("ResourceLinkDescription", request.getResourceDescription());
        targetExchange.getIn().setBody(request.getResults());
        targetExchange.getIn().setHeader("ComparisonGroup", request.getDescription());
    }
}
