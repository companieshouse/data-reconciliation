package uk.gov.companieshouse.reconciliation.function.email;

import java.util.List;
import java.util.Objects;

/**
 * A collection of requests uploading files to S3
 */
public class PublisherResourceRequestWrapper {
    private List<PublisherResourceRequest> requests;

    public PublisherResourceRequestWrapper(List<PublisherResourceRequest> requests) {
        this.requests = requests;
    }

    public List<PublisherResourceRequest> getRequests() {
        return requests;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PublisherResourceRequestWrapper that = (PublisherResourceRequestWrapper) o;
        return Objects.equals(requests, that.requests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requests);
    }
}
