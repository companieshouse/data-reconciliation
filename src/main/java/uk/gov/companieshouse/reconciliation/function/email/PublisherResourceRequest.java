package uk.gov.companieshouse.reconciliation.function.email;

import java.util.Arrays;
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
    private int orderNumber;

    public PublisherResourceRequest(String objectKey, long expirationTimeInMillis, String uploaderEndpoint,
                                    String presignerEndpoint, String resourceDescription, byte[] results, String comparisonGroup, int orderNumber) {
        this.objectKey = objectKey;
        this.expirationTimeInMillis = expirationTimeInMillis;
        this.uploaderEndpoint = uploaderEndpoint;
        this.presignerEndpoint = presignerEndpoint;
        this.resourceDescription = resourceDescription;
        this.results = results;
        this.comparisonGroup = comparisonGroup;
        this.orderNumber = orderNumber;
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

    public Integer getOrderNumber() {
        return orderNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublisherResourceRequest that = (PublisherResourceRequest) o;
        return expirationTimeInMillis == that.expirationTimeInMillis && Objects.equals(objectKey, that.objectKey) && Objects.equals(uploaderEndpoint, that.uploaderEndpoint) && Objects.equals(presignerEndpoint, that.presignerEndpoint) && Objects.equals(resourceDescription, that.resourceDescription) && Objects.equals(comparisonGroup, that.comparisonGroup) && Objects.equals(orderNumber, that.orderNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectKey, expirationTimeInMillis, uploaderEndpoint, presignerEndpoint, resourceDescription, comparisonGroup, orderNumber);
    }
}
