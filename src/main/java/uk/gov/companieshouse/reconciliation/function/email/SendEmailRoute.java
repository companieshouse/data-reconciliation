package uk.gov.companieshouse.reconciliation.function.email;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.email.aggregator.EmailAggregationStrategy;

/**
 * Email broadcast with results gathered from comparison jobs.
 *
 * The following request headers (two being SES2 constants) should be set when a message is sent to this route:
 *
 * Destination: The endpoint to which results will be sent.
 *
 * The response body will contain an concatenated message composed from the body
 * of previous comparison jobs.
 *
 */
@Component
public class SendEmailRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("{{function.name.send_email}}")
                .aggregate(constant(true), new EmailAggregationStrategy())
                .completion(header("AggregationComplete").isEqualTo("true"))
                .log("Email Body: ${body}")
                .toD("${header.Destination}");
    }
}
