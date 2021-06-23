package uk.gov.companieshouse.reconciliation.config;

import uk.gov.companieshouse.reconciliation.model.ResourceLink;

import javax.validation.constraints.NotNull;

/**
 * Email link model that defines a rank that can be used to order {@link ResourceLink}'s.
 */
public class EmailLinkModel {

    @NotNull
    private Short rank;

    @NotNull
    private Boolean enabled;

    public void setRank(Short rank) {
        this.rank = rank;
    }

    public Short getRank() {
        return rank;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
