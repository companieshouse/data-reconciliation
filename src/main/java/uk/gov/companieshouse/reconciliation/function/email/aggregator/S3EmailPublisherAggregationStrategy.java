package uk.gov.companieshouse.reconciliation.function.email.aggregator;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequest;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequestWrapper;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Accumulates results published by comparison functions and maps them to a collection of requests to be sent to S3
 */
public class S3EmailPublisherAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange prev, Exchange curr) {

        Exchange targetExchange = Optional.ofNullable(prev).orElse(curr);

        // Define how many messages a comparison group should have before being marked as complete.
        // Need a way to keep track of how many messages has been processed inside a comparison group.
        // Need a way marking a comparison group to complete after all relevant messages has been processed.
        String comparisonGroup = targetExchange.getIn().getHeader("ComparisonGroup", String.class);
        Integer numberOfMessages = targetExchange.getIn().getHeader(comparisonGroup, Integer.class);

        if (numberOfMessages == null) {
            numberOfMessages = 0;
        }

        targetExchange.getIn().setHeader(comparisonGroup, ++numberOfMessages);

        if (numberOfMessages == getCompletionSizeForGroup(comparisonGroup)) {
            targetExchange.getIn().setHeader(comparisonGroup, 0);
            targetExchange.getIn().setHeader("Completed", true);
        }

        PublisherResourceRequestWrapper resourceRequests = Optional.ofNullable(targetExchange.getIn().getHeader("PublisherResourceRequests", PublisherResourceRequestWrapper.class)).orElse(new PublisherResourceRequestWrapper(new ArrayList<>()));
        resourceRequests.getRequests().add(new PublisherResourceRequest(
                curr.getIn().getHeader(AWS2S3Constants.KEY, String.class),
                curr.getIn().getHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, Long.class),
                curr.getIn().getHeader("Upload", String.class),
                curr.getIn().getHeader("Presign", String.class),
                curr.getIn().getHeader("ResourceLinkDescription", String.class),
                curr.getIn().getBody(byte[].class)));
        targetExchange.getIn().setHeader("PublisherResourceRequests", resourceRequests);
        return targetExchange;
    }

    private int getCompletionSizeForGroup(String group) {
        if ("CompanyProfile".equals(group)) {
            return 2;
        } else if ("Elasticsearch".equals(group)) {
            return 6;
        } else if ("DisqualifiedOfficer".equals(group)) {
            return 1;
        } else {
            throw new IllegalArgumentException("Invalid comparison group specified: " + group);
        }
    }
}
