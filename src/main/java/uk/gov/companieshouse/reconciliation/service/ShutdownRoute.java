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
                .completionSize(aggregationHandler.getEnabledAggregationGroupModelsSize())
                .log(LoggingLevel.INFO, "Triggering application shutdown...")
                .process(exchange -> new Thread(() -> {
                    context.close();
                    // AWS reserved env var, if not set we're probably not in ECS
                    if (!System.getenv().containsKey("AWS_EXECUTION_ENV")) {
                        System.exit(0);
                    }

                    // Workaround to allow ECS to control the lifecycle of this service
                    for (; ; ) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start());
    }
}
