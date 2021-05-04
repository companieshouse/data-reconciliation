package uk.gov.companieshouse.reconciliation.function.email;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.email.aggregator.EmailAggregationStrategy;

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
