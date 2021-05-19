package uk.gov.companieshouse.reconciliation.function.email;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.email.aggregator.EmailAggregationStrategy;

@Component
public class SendDsqOfficerEmailRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:send-dsq_officer-email")
                .aggregate(constant(true), new EmailAggregationStrategy())
                .completionSize(1)
                .setHeader("CompletionDate", simple("${date:now:dd MMMM yyyy}"))
                .setHeader("EmailSubject", simple("Disqualified officer comparisons (${header.CompletionDate})"))
                .to("{{endpoint.kafka.sender}}");
    }
}
