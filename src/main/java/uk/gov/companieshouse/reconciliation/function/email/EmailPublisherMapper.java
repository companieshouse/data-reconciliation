package uk.gov.companieshouse.reconciliation.function.email;

import org.apache.camel.Headers;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;

import java.util.Map;

/**
 * Serialises requests to upload results to S3
 */
public class EmailPublisherMapper {

    public byte[] map(PublisherResourceRequest request, @Headers Map<String, Object> headers) {
        headers.put(AWS2S3Constants.KEY, request.getObjectKey());
        headers.put(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, request.getExpirationTimeInMillis());
        headers.put("Upload", request.getUploaderEndpoint());
        headers.put("Presign", request.getPresignerEndpoint());
        headers.put("ResourceLinkDescription", request.getResourceDescription());
        headers.put("ComparisonGroup", request.getDescription());
        headers.put("Failed", request.isFailed());
        return request.getResults();
    }
}
