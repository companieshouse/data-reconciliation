package uk.gov.companieshouse.reconciliation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class AggregationHandler {

    private Map<String, AbstractAggregationConfiguration> comparisonGroupSizes;

    @Autowired
    public AggregationHandler(CompanyAggregationConfiguration companyAggregationConfiguration,
                              DisqualifiedOfficerConfiguration disqualifiedOfficerConfiguration,
                              ElasticsearchConfiguration elasticsearchConfiguration) {
        this.comparisonGroupSizes = new HashMap<>();
        this.comparisonGroupSizes.put(companyAggregationConfiguration.getGroupName(), companyAggregationConfiguration);
        this.comparisonGroupSizes.put(disqualifiedOfficerConfiguration.getGroupName(), disqualifiedOfficerConfiguration);
        this.comparisonGroupSizes.put(elasticsearchConfiguration.getGroupName(), elasticsearchConfiguration);
        this.comparisonGroupSizes = Collections.unmodifiableMap(this.comparisonGroupSizes);
    }

    public AbstractAggregationConfiguration getAggregationConfiguration(String comparisonGroup) {
        return comparisonGroupSizes.get(comparisonGroup);
    }
}
