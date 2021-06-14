package uk.gov.companieshouse.reconciliation.function.email;

import java.util.List;

public class PublisherResourceRequestWrapper {
    private List<PublisherResourceRequest> requests;

    public PublisherResourceRequestWrapper(List<PublisherResourceRequest> requests) {
        this.requests = requests;
    }

    public List<PublisherResourceRequest> getRequests() {
        return requests;
    }
}
