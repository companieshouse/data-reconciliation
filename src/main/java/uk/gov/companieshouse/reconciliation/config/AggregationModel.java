package uk.gov.companieshouse.reconciliation.config;

import uk.gov.companieshouse.reconciliation.model.ResourceLink;

import javax.validation.constraints.NotNull;

/**
 * Email link model that defines a rank that can be used to order {@link ResourceLink}.
 */
public class AggregationModel {

    @NotNull
    private Short linkRank;

    @NotNull
    private Boolean enabled;

    public void setLinkRank(Short linkRank) {
        this.linkRank = linkRank;
    }

    public Short getLinkRank() {
        return linkRank;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
