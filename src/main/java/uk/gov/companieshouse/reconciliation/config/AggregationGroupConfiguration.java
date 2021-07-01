package uk.gov.companieshouse.reconciliation.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource("classpath:aggregation-groups.properties")
public class AggregationGroupConfiguration {

    /**
     * Builds a map from aggregation-group configuration properties.
     *
     * <p>Note: the Map key for each entry corresponds to the property key e.g.</p>
     *
     * <pre>
aggregation-group.&lt;key1&gt;.groupName = ABC
aggregation-group.&lt;key1&gt;.size = n1
aggregation-group.&lt;key2&gt;.groupName = XYZ
aggregation-group.&lt;key2&gt;.size = n2
...
     * </pre>
     *
     * @return map
     */
    @Bean
    @ConfigurationProperties(prefix = "aggregation-group")
    public Map<String, AggregationGroupModel> aggregationGroupModelMap() {
        return new HashMap<>();
    }
}
