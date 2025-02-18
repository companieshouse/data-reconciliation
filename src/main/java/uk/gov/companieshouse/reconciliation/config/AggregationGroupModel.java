package uk.gov.companieshouse.reconciliation.config;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Model that defines a logical grouping of {@link AggregationModel}.
 *
 * Note that this model also defines a size that represents the number of aggregation models in this model.
 */
@Validated
public class AggregationGroupModel {

    @NotEmpty
    private String groupName;

    @NotNull
    private Map<String, AggregationModel> aggregationModels;

    /**
     * Returns the group name corresponding to the group name of a comparison group.
     *
     * @return group name of this model
     */
    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * @return size representing the number of enabled aggregation models in a comparison group
     */
    public int getEnabledAggregationModelsSize() {
        return aggregationModels.values().stream()
                .filter(aggregationModel -> aggregationModel.isEnabled())
                .collect(Collectors.toSet())
                .size();
    }

    /**
     * Returns Map of {@link AggregationModel} indexed by aggregationModelId.
     *
     * @return map
     */
    public Map<String, AggregationModel> getAggregationModels() {
        return aggregationModels;
    }

    public void setAggregationGroupModel(Map<String, AggregationModel> aggregationModels) {
        this.aggregationModels = aggregationModels;
    }
}