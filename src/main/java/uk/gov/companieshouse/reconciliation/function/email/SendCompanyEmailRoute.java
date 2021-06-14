package uk.gov.companieshouse.reconciliation.function.email;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.email.aggregator.EmailAggregationStrategy;
import uk.gov.companieshouse.reconciliation.function.email.aggregator.S3EmailPublisherAggregationStrategy;

/**
 * Sends an email comprising of company profile results gathered from comparison jobs.
 */
@Component
public class SendCompanyEmailRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:send-company-email")
                .aggregate(header("ComparisonGroup"), new S3EmailPublisherAggregationStrategy())
                .completion(header("Completed").isEqualTo(true))
                .split(method(EmailPublisherSplitter.class), new EmailAggregationStrategy())
                .bean(EmailPublisherMapper.class)
                .to("direct:s3-publisher")
                .end()
                .setHeader("CompletionDate", simple("${date:now:dd MMMM yyyy}"))
                .setHeader("EmailSubject", simple("Company profile comparisons (${header.CompletionDate})"))
                .to("{{endpoint.kafka.sender}}");
    }
}
