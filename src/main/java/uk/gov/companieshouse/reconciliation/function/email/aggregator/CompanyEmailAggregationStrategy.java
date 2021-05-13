package uk.gov.companieshouse.reconciliation.function.email.aggregator;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Aggregates comparison messages into a {@link uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper collection of links}.
 */
public class CompanyEmailAggregationStrategy implements AggregationStrategy {

    private static final String RESOURCE_LINKS_HEADER = "ResourceLinks";
    private static final String COMPARE_COUNT_LINK_HEADER = "CompareCountLink";
    private static final String COMPARE_COLLECTION_LINK_HEADER = "CompareCollectionLink";
    private static final String COMPARE_COUNT_DESCRIPTION_HEADER = "CompareCountDescription";
    private static final String COMPARE_COLLECTION_DESCRIPTION_HEADER = "CompareCollectionDescription";

    /**
     * Aggregates comparison messages.<br>
     * <br>
     * IN:<br>
     * header(CompareCountLink) - A link to the results of a comparison between resource counts.<br>
     * header(CompareCountDescription) - A description of the resource to which the link relates.<br>
     * header(CompareCollectionLink) - A link to the results of a comparison between resource collections.<br>
     * header(CompareCollectionDescription) - A description of the resource to which the link relates.<br>
     * <br>
     * OUT:<br>
     * header(ResourceLinks) - A {@link ResourceLinksWrapper collection of links}.<br>
     * <br>
     * @param oldExchange   A {@link Exchange to the previous exchange} which will be aggregated to a final result.
     * @param newExchange   A {@link Exchange to the next exchange} which will be aggregated to final result.
     * @return A {@link Exchange} representing the aggregated results of both exchanges.
     */
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        //on first invocation, oldExchange will be null as this is the first message that we are aggregating
        Exchange exchange = Optional.ofNullable(oldExchange).orElse(newExchange);
        ResourceLinksWrapper downloadLinks = createOrGetResourceLinks(exchange);
        Optional<String> compareCountLink = header(newExchange, COMPARE_COUNT_LINK_HEADER);
        Optional<String> compareCollectionLink = header(newExchange, COMPARE_COLLECTION_LINK_HEADER);
        if (compareCountLink.isPresent()) {
            downloadLinks.addDownloadLink(compareCountLink.get(), newExchange.getIn().getHeader(COMPARE_COUNT_DESCRIPTION_HEADER, String.class));
        } else if (compareCollectionLink.isPresent()) {
            downloadLinks.addDownloadLink(compareCollectionLink.get(), newExchange.getIn().getHeader(COMPARE_COLLECTION_DESCRIPTION_HEADER, String.class));
        } else {
            throw new IllegalArgumentException("Expected links not present: CompareCountLink, CompareCollectionLink");
        }
        return exchange;
    }

    private ResourceLinksWrapper createOrGetResourceLinks(Exchange exchange) {
        ResourceLinksWrapper resourceLinks = exchange.getIn().getHeader(RESOURCE_LINKS_HEADER, ResourceLinksWrapper.class);
        if (resourceLinks == null) {
            resourceLinks = new ResourceLinksWrapper(new ArrayList<>());
            exchange.getIn().setHeader(RESOURCE_LINKS_HEADER, resourceLinks);
        }
        return resourceLinks;
    }

    private Optional<String> header(Exchange exchange, String headerName) {
        return Optional.ofNullable(exchange.getIn().getHeader(headerName, String.class));
    }
}