package uk.gov.companieshouse.reconciliation.config;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Supplies {@link AggregationGroupModel}'s for given comparison group names.
 */
public class AggregationHandler {

    @NotNull
    private Map<String, AggregationGroupModel> comparisonGroupConfigMap;

    /**
     * Constructs an AggregationHandler from a Map.
     *
     * <p>Note: the Map key must contain the groupName of each value.</p>
     *
     * @param comparisonGroupConfigMap
     */
    public AggregationHandler(Map<String, AggregationGroupModel> comparisonGroupConfigMap) {
        this.comparisonGroupConfigMap = comparisonGroupConfigMap;
    }

    /**
     * @param groupName of the config to return
     * @return ComparisonGroupConfig with the given groupName, null otherwise
     */
    public AggregationGroupModel getAggregationConfiguration(String groupName) {
        return comparisonGroupConfigMap.get(groupName);
    }

    public int getNumberOfComparisonGroups() {
        return comparisonGroupConfigMap.values().stream()
                .filter(comparisonGroupModel -> comparisonGroupModel.getSize() > 0)
                .collect(Collectors.toSet())
                .size();
    }
}
