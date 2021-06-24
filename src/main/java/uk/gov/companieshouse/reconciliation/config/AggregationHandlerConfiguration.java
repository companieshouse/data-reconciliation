package uk.gov.companieshouse.reconciliation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@ComponentScan("uk.gov.companieshouse.reconciliation.config")
public class AggregationHandlerConfiguration {

    private Map<String, ComparisonGroupModel> comparisonGroupConfigMap;

    @Autowired
    public AggregationHandlerConfiguration(Map<String, ComparisonGroupModel> comparisonGroupConfigMap) {
        this.comparisonGroupConfigMap = comparisonGroupConfigMap;
    }

    @Bean
    @Validated
    public AggregationHandler aggregationHandler() {
        return new AggregationHandler(comparisonGroupConfigMap.values()
                .stream()
                .collect(Collectors.toMap(ComparisonGroupModel::getGroupName, Function.identity())));
    }
}
