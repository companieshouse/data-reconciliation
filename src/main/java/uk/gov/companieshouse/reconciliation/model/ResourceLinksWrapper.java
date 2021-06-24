package uk.gov.companieshouse.reconciliation.model;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Aggregates {@link ResourceLink download links} to comparison results.
 */
public class ResourceLinksWrapper {

    private final Set<ResourceLink> downloadLinkSet;

    /**
     * Creates a new ResourceLinksWrapper.
     *
     * @param downloadLinkSet of resource links
     */
    public ResourceLinksWrapper(Set<ResourceLink> downloadLinkSet) {
        this.downloadLinkSet = downloadLinkSet;
    }

    /**
     * Add a download link to comparison results.
     *
     * @param link A link to a resource.
     * @param description A description of the resource that the link relates to.
     */
    public void addDownloadLink(short rank, String link, String description){
        this.downloadLinkSet.add(new ResourceLink(rank, link, description));
    }

    /**
     * @return A view of all {@link ResourceLink links} that belong to this collection.
     */
    public Set<ResourceLink> getDownloadLinkSet() {
        return Collections.unmodifiableSet(downloadLinkSet);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceLinksWrapper that = (ResourceLinksWrapper) o;
        return Objects.equals(downloadLinkSet, that.downloadLinkSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(downloadLinkSet);
    }
}
