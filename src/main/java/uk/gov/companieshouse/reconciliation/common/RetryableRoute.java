package uk.gov.companieshouse.reconciliation.common;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Redelivers messages in the event of an exception being thrown. The maximum allowed number of message redeliveries is
 * configured by property wrapper.retries.
 */
@Component
public class RetryableRoute extends RouteBuilder {

    @Value("${wrapper.retries}")
    private int retries;

    @Override
    public void configure() {
        errorHandler(defaultErrorHandler().maximumRedeliveries(retries));
    }
}
