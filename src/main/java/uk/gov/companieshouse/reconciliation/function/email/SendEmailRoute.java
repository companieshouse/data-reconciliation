package uk.gov.companieshouse.reconciliation.function.email;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.email.aggregator.EmailAggregationStrategy;
import uk.gov.companieshouse.reconciliation.function.email.aggregator.S3EmailPublisherAggregationStrategy;

/**
 * Sends an email comprising of company profile results gathered from comparison jobs.
 */
@Component
public class SendEmailRoute extends RouteBuilder {

    @Autowired
    private S3EmailPublisherAggregationStrategy s3EmailPublisherAggregationStrategy;

    @Override
    public void configure() throws Exception {

        from("direct:send-email")
                .aggregate(header("ComparisonGroup"), s3EmailPublisherAggregationStrategy)
                .completion(header("Completed").isEqualTo(true))
                .split(method(EmailPublisherSplitter.class), new EmailAggregationStrategy())
                .bean(EmailPublisherMapper.class)
                .to("direct:s3-publisher")
                .end()
                .setHeader("CompletionDate", simple("${date:now:dd MMMM yyyy}"))
                .setHeader("EmailSubject", simple("${header.ComparisonGroup} comparisons (${header.CompletionDate})"))
                .to("{{endpoint.kafka.sender}}");
    }
}
