package uk.gov.companieshouse.reconciliation.function.email.aggregator;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequest;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequestWrapper;

import java.util.ArrayList;
import java.util.Optional;

public class EmailPublisherAggregationStrategy implements AggregationStrategy {
    @Override
    public Exchange aggregate(Exchange prev, Exchange curr) {
        Exchange targetExchange = Optional.ofNullable(prev).orElse(curr);
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
}
