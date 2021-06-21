package uk.gov.companieshouse.reconciliation.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Aggregates {@link ResourceLink download links} to comparison results.
 */
public class ResourceLinksWrapper {

    private final Set<ResourceLink> downloadLinkList;

    public ResourceLinksWrapper(Set<ResourceLink> downloadLinkList) {
        this.downloadLinkList = downloadLinkList;
    }

    /**
     * Add a download link to comparison results.
     *
     * @param link A link to a resource.
     * @param description A description of the resource that the link relates to.
     */
    public void addDownloadLink(short rank, String link, String description){
        this.downloadLinkList.add(new ResourceLink(rank, link, description));
    }

    /**
     * @return A view of all {@link ResourceLink links} that belong to this collection.
     */
    public List<ResourceLink> getDownloadLinkList() {
        return Collections.unmodifiableList(new ArrayList<>(downloadLinkList));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResourceLinksWrapper that = (ResourceLinksWrapper) o;
        return Objects.equals(downloadLinkList, that.downloadLinkList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(downloadLinkList);
    }
}
