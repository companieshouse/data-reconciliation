package uk.gov.companieshouse.reconciliation.function.email;

import java.util.Objects;

/**
 *  Request information needed to upload a file to S3
 */
public class PublisherResourceRequest {

    private final String objectKey;
    private final long expirationTimeInMillis;
    private final String uploaderEndpoint;
    private final String presignerEndpoint;
    private final String resourceDescription;
    private final byte[] results;
    private final String comparisonGroup;
    private final boolean failed;

    public PublisherResourceRequest(String objectKey, long expirationTimeInMillis, String uploaderEndpoint,
                                    String presignerEndpoint, String resourceDescription, byte[] results, String comparisonGroup, boolean failed) {
        this.objectKey = objectKey;
        this.expirationTimeInMillis = expirationTimeInMillis;
        this.uploaderEndpoint = uploaderEndpoint;
        this.presignerEndpoint = presignerEndpoint;
        this.resourceDescription = resourceDescription;
        this.results = results;
        this.comparisonGroup = comparisonGroup;
        this.failed = failed;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public long getExpirationTimeInMillis() {
        return expirationTimeInMillis;
    }

    public String getUploaderEndpoint() {
        return uploaderEndpoint;
    }

    public String getPresignerEndpoint() {
        return presignerEndpoint;
    }

    public String getResourceDescription() {
        return resourceDescription;
    }

    public byte[] getResults() {
        return results;
    }

    public String getDescription() {
        return comparisonGroup;
    }

    public boolean isFailed() {
        return failed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublisherResourceRequest)) return false;
        PublisherResourceRequest that = (PublisherResourceRequest) o;
        return getExpirationTimeInMillis() == that.getExpirationTimeInMillis() &&
                isFailed() == that.isFailed() &&
                Objects.equals(getObjectKey(), that.getObjectKey()) &&
                Objects.equals(getUploaderEndpoint(), that.getUploaderEndpoint()) &&
                Objects.equals(getPresignerEndpoint(), that.getPresignerEndpoint()) &&
                Objects.equals(getResourceDescription(), that.getResourceDescription()) &&
                Objects.equals(comparisonGroup, that.comparisonGroup);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getObjectKey(), getExpirationTimeInMillis(), getUploaderEndpoint(), getPresignerEndpoint(), getResourceDescription(), comparisonGroup, isFailed());
    }
}
