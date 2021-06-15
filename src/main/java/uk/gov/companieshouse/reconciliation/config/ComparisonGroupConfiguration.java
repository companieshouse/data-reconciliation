package uk.gov.companieshouse.reconciliation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:comparison-groups.properties")
public class ComparisonGroupConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "aggregation.completion.company")
    public ComparisonGroupConfig companyConfig() {
        return new ComparisonGroupConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "aggregation.completion.dsq")
    public ComparisonGroupConfig dsqConfig() {
        return new ComparisonGroupConfig();
    }

    @Bean
    @ConfigurationProperties(prefix = "aggregation.completion.elasticsearch")
    public ComparisonGroupConfig elasticsearchConfig() {
        return new ComparisonGroupConfig();
    }
}
