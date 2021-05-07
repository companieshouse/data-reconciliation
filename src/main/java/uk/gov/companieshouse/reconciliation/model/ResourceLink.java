package uk.gov.companieshouse.reconciliation.model;

public class ResourceLink {

    private String link;
    private String description;

    public ResourceLink(String link, String description) {
        this.link = link;
        this.description = description;
    }

    public String getDownloadLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }
}
