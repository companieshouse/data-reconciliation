package uk.gov.companieshouse.reconciliation.config;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Supplies {@link AggregationGroupModel}'s for given comparison group names.
 */
public class AggregationHandler {

    @NotNull
    private Map<String, AggregationGroupModel> aggregationGroupModel;

    /**
     * Constructs an AggregationHandler from a Map.
     *
     * <p>Note: the Map key must contain the groupName of each value.</p>
     *
     * @param aggregationGroupModel
     */
    public AggregationHandler(Map<String, AggregationGroupModel> aggregationGroupModel) {
        this.aggregationGroupModel = aggregationGroupModel;
    }

    /**
     * @param groupName of the config to return
     * @return AggregationGroupMode with the given groupName, null otherwise
     */
    public AggregationGroupModel getAggregationConfiguration(String groupName) {
        return aggregationGroupModel.get(groupName);
    }

    public int getNumberOfComparisonGroups() {
        return aggregationGroupModel.values().stream()
                .filter(aggregationGroupModel -> aggregationGroupModel.getSize() > 0)
                .collect(Collectors.toSet())
                .size();
    }
}
