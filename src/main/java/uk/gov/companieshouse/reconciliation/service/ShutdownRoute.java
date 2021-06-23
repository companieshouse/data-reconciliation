package uk.gov.companieshouse.reconciliation.service;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.config.AggregationHandler;

/**
 * Shuts down the application after all comparisons have been performed.
 */
@Component
public class ShutdownRoute extends RouteBuilder {

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private AggregationHandler aggregationHandler;

    @Override
    public void configure() throws Exception {
        from("direct:shutdown")
                .aggregate()
                .constant(true)
                .aggregationStrategy(AggregationStrategies.useLatest())
                .completionSize(aggregationHandler.getNumberOfComparisonGroups())
                .log(LoggingLevel.INFO, "Triggering application shutdown...")
                .process(exchange -> new Thread(() -> {
                    context.close();
                    System.exit(0);
                }).start());
    }
}
