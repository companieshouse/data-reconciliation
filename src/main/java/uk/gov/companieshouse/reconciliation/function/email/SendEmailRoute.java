package uk.gov.companieshouse.reconciliation.function.email;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.email.aggregator.EmailAggregationStrategy;

/**
 * Sends an email with results gathered from comparison jobs.
 */
@Component
public class SendEmailRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:send-email")
                .aggregate(constant(true), new EmailAggregationStrategy())
                .completionSize(2)
                .to("{{endpoint.kafka.sender}}");
    }
}
