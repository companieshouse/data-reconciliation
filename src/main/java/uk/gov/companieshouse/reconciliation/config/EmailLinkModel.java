package uk.gov.companieshouse.reconciliation.config;

import javax.validation.constraints.NotNull;

public class EmailLinkModel {

    @NotNull
    private Short rank;

    public void setRank(Short rank) {
        this.rank = rank;
    }

    public Short getRank() {
        return rank;
    }
}
