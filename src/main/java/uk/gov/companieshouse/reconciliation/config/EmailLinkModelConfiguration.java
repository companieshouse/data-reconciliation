package uk.gov.companieshouse.reconciliation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("classpath:email-link-ordering.properties")
public class EmailLinkModelConfiguration {

    @Bean("emailLinksModelMap")
    @ConfigurationProperties(prefix = "comparison-email")
    public Map<String, Map<String, EmailLinkModel>> emailLinksModelMap() {
        return new HashMap<>();
    }
}
