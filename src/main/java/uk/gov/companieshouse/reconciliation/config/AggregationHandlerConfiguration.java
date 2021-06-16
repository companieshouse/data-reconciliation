package uk.gov.companieshouse.reconciliation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@ComponentScan("uk.gov.companieshouse.reconciliation.config")
public class AggregationHandlerConfiguration {
    private Map<String, ComparisonGroupConfig> comparisonGroupConfigMap;

    @Autowired
    public AggregationHandlerConfiguration(Map<String, ComparisonGroupConfig> comparisonGroupConfigMap) {
        this.comparisonGroupConfigMap = comparisonGroupConfigMap;
    }

    @Bean
    public AggregationHandler aggregationHandler() {
        return new AggregationHandler(comparisonGroupConfigMap.values()
                .stream()
                .collect(Collectors.toMap(ComparisonGroupConfig::getGroupName, Function.identity())));
    }
}
