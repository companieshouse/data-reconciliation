package uk.gov.companieshouse.reconciliation.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 *
 * Configures the spring bean required for the {@link AggregationHandler}
 *
 */
@Configuration
@ComponentScan("uk.gov.companieshouse.reconciliation.config")
public class AggregationHandlerConfiguration {

    private Map<String, AggregationGroupModel> aggregationGroupModels;

    /**
     *
     * Returns a map of the {@link AggregationGroupModel}
     *
     * @param aggregationGroupModels
     */
    @Autowired
    public AggregationHandlerConfiguration(Map<String, AggregationGroupModel> aggregationGroupModels) {
        this.aggregationGroupModels = aggregationGroupModels;
    }

    /**
     *
     * @return values defined in aggregation-group.properties that represents valid comparison groups.
     */
    @Bean
    public AggregationHandler aggregationHandler() {
        return new AggregationHandler(aggregationGroupModels.values()
                .stream()
                .collect(Collectors.toMap(AggregationGroupModel::getGroupName, Function.identity())));
    }
}
