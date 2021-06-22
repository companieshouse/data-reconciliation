package uk.gov.companieshouse.reconciliation.model;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Aggregates {@link ResourceLink download links} to comparison results.
 */
public class ResourceLinksWrapper {

    private final List<ResourceLink> downloadLinkList;
    private Comparator<ResourceLink> comparator;

    /**
     * Creates a new ResourceLinksWrapper.
     *
     * @param downloadLinkList of resource links
     */
    public ResourceLinksWrapper(List<ResourceLink> downloadLinkList) {
        this.downloadLinkList = downloadLinkList;
    }

    /**
     * Creates a new ResourceLinksWrapper.
     *
     * <p>The Comparator will be used to sort the links contained in this wrapper.</p>
     *
     * @param downloadLinkList of resource links
     * @param comparator to sort links
     */
    public ResourceLinksWrapper(List<ResourceLink> downloadLinkList, Comparator<ResourceLink> comparator) {
        this.downloadLinkList = downloadLinkList;
        this.comparator = comparator;
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
        if (comparator != null) {
            Collections.sort(downloadLinkList, comparator);
        }
        return Collections.unmodifiableList(downloadLinkList);
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
