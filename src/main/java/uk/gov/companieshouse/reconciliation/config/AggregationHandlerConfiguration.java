package uk.gov.companieshouse.reconciliation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan("uk.gov.companieshouse.reconciliation.config")
public class AggregationHandlerConfiguration {
    private ComparisonGroupConfig companyConfig;
    private ComparisonGroupConfig dsqConfig;
    private ComparisonGroupConfig elasticsearchConfig;

    @Autowired
    public AggregationHandlerConfiguration(ComparisonGroupConfig companyConfig, ComparisonGroupConfig dsqConfig, ComparisonGroupConfig elasticsearchConfig) {
        this.companyConfig = companyConfig;
        this.dsqConfig = dsqConfig;
        this.elasticsearchConfig = elasticsearchConfig;
    }

    @Bean
    public AggregationHandler aggregationHandler() {
        Map<String, ComparisonGroupConfig> map = new HashMap<>();

        map.put(companyConfig.getGroupName(), companyConfig);
        map.put(dsqConfig.getGroupName(), dsqConfig);
        map.put(elasticsearchConfig.getGroupName(), elasticsearchConfig);

        return new AggregationHandler(map);
    }
}
