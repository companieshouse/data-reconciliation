package uk.gov.companieshouse.reconciliation.service;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Shuts down the application after all comparisons have been performed.
 */
@Component
public class ShutdownRoute extends RouteBuilder {

    @Autowired
    private ConfigurableApplicationContext context;

    @Override
    public void configure() throws Exception {
        from("direct:shutdown")
                .aggregate()
                .constant(true)
                .aggregationStrategy(AggregationStrategies.useLatest())
                .completionSize(4)
                .log(LoggingLevel.INFO, "Triggering application shutdown...")
                .process(exchange -> new Thread(() -> {
                    context.close();
                    System.exit(0);
                }).start());
    }
}
