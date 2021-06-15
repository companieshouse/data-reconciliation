package uk.gov.companieshouse.reconciliation.config;

import java.util.Map;

public class AggregationHandler {

    private Map<String, ComparisonGroupConfig> groupConfigMap;

    public AggregationHandler(Map<String, ComparisonGroupConfig> groupConfigMap) {
        this.groupConfigMap = groupConfigMap;
    }

    public ComparisonGroupConfig getAggregationConfiguration(String comparisonGroup) {
        return groupConfigMap.get(comparisonGroup);
    }
}
