package uk.gov.companieshouse.reconciliation.model;

/**
 * A link to a resource and a description of that resource.
 */
public class ResourceLink {

    private final String linkId;
    private final String downloadLink;
    private final String description;

    public ResourceLink(String linkId, String downloadLink, String description) {
        this.linkId = linkId;
        this.downloadLink = downloadLink;
        this.description = description;
    }

    public String getLinkId() {
        return linkId;
    }

    /**
     * @return A link to a resource.
     */
    public String getDownloadLink() {
        return downloadLink;
    }

    /**
     * @return A description of the resource that the link relates to.
     */
    public String getDescription() {
        return description;
    }
}
