package uk.gov.companieshouse.reconciliation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:comparison-groups.properties")
@ConfigurationProperties(prefix = "aggregation.completion.dsq")
public class DisqualifiedOfficerConfiguration extends AbstractAggregationConfiguration {

}
