package uk.gov.companieshouse.reconciliation.function.email;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.config.AggregationHandler;
import uk.gov.companieshouse.reconciliation.function.email.aggregator.EmailAggregationStrategy;
import uk.gov.companieshouse.reconciliation.function.email.aggregator.S3EmailPublisherAggregationStrategy;

/**
 * Sends an email comprising of company profile results gathered from comparison jobs.
 */
@Component
public class SendEmailRoute extends RouteBuilder {

    @Autowired
    private S3EmailPublisherAggregationStrategy s3EmailPublisherAggregationStrategy;

    @Autowired
    private AggregationHandler aggregationHandler;

    @Override
    public void configure() throws Exception {

        from("direct:send-email")
                .aggregate(header("ComparisonGroup"), s3EmailPublisherAggregationStrategy)
                .completion(header("Completed").isEqualTo(true))
                .split(method(EmailPublisherSplitter.class), new EmailAggregationStrategy(aggregationHandler))
                    .bean(EmailPublisherMapper.class)
                    .choice()
                    .when(header("Failed").isNotEqualTo(true))
                        .enrich("direct:s3-publisher", (prev, curr) -> {
                            if(curr.getIn().getHeader("Failed", boolean.class)) {
                                prev.getIn().setHeader("ResourceLinkReference", null);
                                prev.getIn().setHeader("ResourceLinkDescription", String.format("Failed to upload results for %s to S3.", curr.getIn().getHeader("ComparisonDescription", String.class)));
                            } else {
                                prev.getIn().setHeader("ResourceLinkReference", curr.getIn().getHeader("ResourceLinkReference"));
                            }
                            return prev;
                        })
                    .otherwise()
                        .log("Not publishing ${header.ComparisonDescription} to S3 as it has failed.")
                    .end()
                .end()
                .setHeader("CompletionDate", simple("${date:now:dd MMMM yyyy}"))
                .setHeader("EmailSubject", simple("${header.ComparisonGroup} comparisons (${header.CompletionDate})"))
                .to("{{endpoint.kafka.sender}}");
    }
}
