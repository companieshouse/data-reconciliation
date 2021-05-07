package uk.gov.companieshouse.reconciliation.model;

import java.util.Collections;
import java.util.List;

/**
 * Aggregates {@link ResourceLink download links} to comparison results.
 */
public class ResourceLinksWrapper {

    private final List<ResourceLink> downloadLinkList;

    public ResourceLinksWrapper(List<ResourceLink> downloadLinkList) {
        this.downloadLinkList = downloadLinkList;
    }

    /**
     * Add a download link to comparison results.
     *
     * @param link A link to a resource.
     * @param description A description of the resource that the link relates to.
     */
    public void addDownloadLink(String link, String description){
        this.downloadLinkList.add(new ResourceLink(link, description));
    }

    /**
     * @return A view of all {@link ResourceLink links} that belong to this collection.
     */
    public List<ResourceLink> getDownloadLinkList() {
        return Collections.unmodifiableList(downloadLinkList);
    }
}
