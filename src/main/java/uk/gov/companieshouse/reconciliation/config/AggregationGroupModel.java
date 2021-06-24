package uk.gov.companieshouse.reconciliation.config;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Model that defines a logical grouping of {@link AggregationModel}'s.
 *
 * Note that this model also defines a size that represents the number of email link models in this model.
 */
public class AggregationGroupModel {

    @NotEmpty
    private String groupName;

    @NotNull
    private Map<String, AggregationModel> aggregationGroupModel;

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
     * @return size representing the number email link models in a comparison group
     */
    public int getSize() {
        return aggregationGroupModel.values().stream()
                .filter(aggregationModel -> aggregationModel.isEnabled())
                .collect(Collectors.toSet())
                .size();
    }

    /**
     * Returns Map of {@link AggregationModel}'s indexed by emailId.
     *
     * @return map
     */
    public Map<String, AggregationModel> getAggregationGroupModel() {
        return aggregationGroupModel;
    }

    public void setAggregationGroupModel(Map<String, AggregationModel> aggregationGroupModel) {
        this.aggregationGroupModel = aggregationGroupModel;
    }
}