package uk.gov.companieshouse.reconciliation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("classpath:comparison-groups.properties")
public class ComparisonGroupConfiguration {

    /**
     * Builds a map from aggregation.completion configuration properties.
     *
     * <p>Note: the Map key for each entry corresponds to the property key e.g.</p>
     *
     * <pre>
aggregation.completion.&lt;key1&gt;.groupName = ABC
aggregation.completion.&lt;key1&gt;.size = n1
aggregation.completion.&lt;key2&gt;.groupName = XYZ
aggregation.completion.&lt;key2&gt;.size = n2
...
     * </pre>
     *
     * @return map
     */
    @Bean
    @Validated
    @ConfigurationProperties(prefix = "aggregation.completion")
    public Map<String, ComparisonGroupModel> comparisonGroupConfigMap() {
        return new HashMap<>();
    }
}
