package uk.gov.companieshouse.reconciliation.function.email.aggregator;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import java.util.ArrayList;
import java.util.Optional;

public class DsqOfficerEmailAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        ResourceLinksWrapper downloadLinks = new ResourceLinksWrapper(new ArrayList<>());
        String downloadLink = Optional.ofNullable(newExchange.getIn().getHeader("ResourceLinkReference", String.class))
                .orElseThrow(() -> new IllegalArgumentException("Expected link not present: ResourceLinkReference"));
        downloadLinks.addDownloadLink(downloadLink, newExchange.getIn().getHeader("ResourceLinkDescription", String.class));
        newExchange.getIn().setHeader("ResourceLinks", downloadLinks);
        return newExchange;
    }
}
