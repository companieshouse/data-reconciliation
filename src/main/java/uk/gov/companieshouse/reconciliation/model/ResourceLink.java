package uk.gov.companieshouse.reconciliation.model;

import java.util.Objects;

/**
 * A link to a resource and a description of that resource.
 */
public class ResourceLink {

    private final short rank;
    private final String downloadLink;
    private final String description;

    public ResourceLink(short rank, String downloadLink, String description) {
        this.rank = rank;
        this.downloadLink = downloadLink;
        this.description = description;
    }

    /**
     * @return rank defining ordering of this link in its group
     */
    public short getRank() {
        return rank;
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
