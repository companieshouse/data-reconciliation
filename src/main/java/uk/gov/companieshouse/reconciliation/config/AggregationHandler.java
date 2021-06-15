package uk.gov.companieshouse.reconciliation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class AggregationHandler {

    private Map<String, ComparisonGroupConfig> groupConfigMap;

    @Autowired
    public AggregationHandler(Map<String, ComparisonGroupConfig> groupConfigMap) {
        this.groupConfigMap = groupConfigMap;
    }

    public ComparisonGroupConfig getAggregationConfiguration(String comparisonGroup) {
        return groupConfigMap.get(comparisonGroup);
    }
}
