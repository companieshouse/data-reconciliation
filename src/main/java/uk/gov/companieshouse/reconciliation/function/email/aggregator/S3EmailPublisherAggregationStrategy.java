package uk.gov.companieshouse.reconciliation.function.email.aggregator;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.config.AggregationHandler;
import uk.gov.companieshouse.reconciliation.config.ComparisonGroupModel;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequest;
import uk.gov.companieshouse.reconciliation.function.email.PublisherResourceRequestWrapper;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Accumulates results published by comparison functions and maps them to a collection of requests to be sent to S3
 */
@Component
public class S3EmailPublisherAggregationStrategy implements AggregationStrategy {

    private AggregationHandler configuration;

    @Autowired
    public S3EmailPublisherAggregationStrategy(AggregationHandler configuration) {
        this.configuration = configuration;
    }

    @Override
    public Exchange aggregate(Exchange prev, Exchange curr) {

        Exchange targetExchange = Optional.ofNullable(prev).orElse(curr);

        String comparisonGroup = curr.getIn().getHeader("ComparisonGroup", String.class);
        Integer numberOfMessages = Optional.ofNullable(targetExchange.getIn().getHeader(comparisonGroup, Integer.class)).orElse(0);

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
                curr.getIn().getBody(byte[].class), comparisonGroup,
                curr.getIn().getHeader("LinkId", String.class),
                curr.getIn().getHeader("Failed", boolean.class)));
        targetExchange.getIn().setHeader("PublisherResourceRequests", resourceRequests);
        return targetExchange;
    }

    private int getCompletionSizeForGroup(String group) {
        Optional<ComparisonGroupModel> comparisonGroupModel = Optional.ofNullable(configuration.getAggregationConfiguration(group));

        if (! comparisonGroupModel.isPresent()) {
            throw new IllegalArgumentException("Mandatory configuration not present ComparisonGroupModel: " + group);
        }

        return comparisonGroupModel.map(ComparisonGroupModel::getSize)
                .orElseThrow(() -> new IllegalArgumentException("Invalid comparison group specified: " + group));
    }
}
