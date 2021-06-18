package uk.gov.companieshouse.reconciliation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("classpath:email-link-ordering.properties")
public class ComparisonEmailLinkOrderingConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "comparison-email")
    public Map<String, Map<String, LinkModel>> comparisonEmailLinkConfigMap() {
        return new HashMap<>();
    }
}
