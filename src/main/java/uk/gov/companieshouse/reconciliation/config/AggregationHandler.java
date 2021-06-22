package uk.gov.companieshouse.reconciliation.config;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * Supplies {@link ComparisonGroupModel}'s for given comparison group names.
 */
public class AggregationHandler {

    @NotNull
    private Map<String, ComparisonGroupModel> comparisonGroupConfigMap;

    /**
     * Constructs an AggregationHandler from a Map.
     *
     * <p>Note: the Map key must contain the groupName of each value.</p>
     *
     * @param comparisonGroupConfigMap
     */
    public AggregationHandler(Map<String, ComparisonGroupModel> comparisonGroupConfigMap) {
        this.comparisonGroupConfigMap = comparisonGroupConfigMap;
    }

    /**
     * @param groupName of the config to return
     * @return ComparisonGroupConfig with the given groupName, null otherwise
     */
    @Validated
    public ComparisonGroupModel getAggregationConfiguration(String groupName) {
        return comparisonGroupConfigMap.get(groupName);
    }
}
