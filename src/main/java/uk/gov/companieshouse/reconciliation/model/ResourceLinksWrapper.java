package uk.gov.companieshouse.reconciliation.model;

import java.util.Collections;
import java.util.List;

public class ResourceLinksWrapper {

    private List<ResourceLink> downloadLinkList;

    public ResourceLinksWrapper(List<ResourceLink> downloadLinkList) {
        this.downloadLinkList = downloadLinkList;
    }

    public boolean addDownloadLink(ResourceLink resourceLink) {
        return downloadLinkList.add(resourceLink);
    }

    public List<ResourceLink> getDownloadLinkList() {
        return Collections.unmodifiableList(downloadLinkList);
    }

}
