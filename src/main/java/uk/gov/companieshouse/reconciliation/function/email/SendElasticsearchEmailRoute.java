package uk.gov.companieshouse.reconciliation.function.email;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.email.aggregator.EmailAggregationStrategy;

@Component
public class SendElasticsearchEmailRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:send-elasticsearch-email")
                .aggregate(constant(true), new EmailAggregationStrategy())
                .completionSize(4)
                .setHeader("CompletionDate", simple("${date:now:dd MMMM yyyy}"))
                .setHeader("EmailSubject", simple("Elasticsearch comparisons (${header.CompletionDate})"))
                .to("{{endpoint.kafka.sender}}");
    }
}
