package uk.gov.companieshouse.reconciliation.config;

import org.springframework.validation.annotation.Validated;
import uk.gov.companieshouse.reconciliation.model.ResourceLink;

import javax.validation.constraints.NotNull;

/**
 * Aggregation model that defines a rank that can be used to order {@link ResourceLink}.
 * Also contains a boolean field to represent whether the model is enabled or not.
 */
@Validated
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
