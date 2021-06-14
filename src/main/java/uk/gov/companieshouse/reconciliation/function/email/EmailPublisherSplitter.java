package uk.gov.companieshouse.reconciliation.function.email;

import org.apache.camel.Header;

import java.util.List;

public class EmailPublisherSplitter {

    public List<PublisherResourceRequest> split(@Header("PublisherResourceRequests") PublisherResourceRequestWrapper resourceRequestWrapper) {
        return resourceRequestWrapper.getRequests();
    }
}
