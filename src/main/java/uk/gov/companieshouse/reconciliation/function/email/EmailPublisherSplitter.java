package uk.gov.companieshouse.reconciliation.function.email;

import org.apache.camel.Header;

import java.util.List;

/**
 * Converts a request wrapper into an iterable collection of requests to upload files to S3
 */
public class EmailPublisherSplitter {

    public List<PublisherResourceRequest> split(@Header("PublisherResourceRequests") PublisherResourceRequestWrapper resourceRequestWrapper) {
        return resourceRequestWrapper.getRequests();
    }
}
