package uk.gov.companieshouse.reconciliation.function.email.aggregator;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import uk.gov.companieshouse.reconciliation.config.AggregationHandler;
import uk.gov.companieshouse.reconciliation.config.ComparisonGroupModel;
import uk.gov.companieshouse.reconciliation.config.EmailLinkModel;
import uk.gov.companieshouse.reconciliation.model.ResourceLink;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import java.util.Comparator;
import java.util.Optional;
import java.util.TreeSet;

/**
 * Aggregates comparison messages into a {@link uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper collection of links}.
 */
public class EmailAggregationStrategy implements AggregationStrategy {

    private static final String RESOURCE_LINKS_HEADER = "ResourceLinks";
    private static final String LINK_REFERENCE_HEADER = "ResourceLinkReference";
    private static final String LINK_DESCRIPTION_HEADER = "ResourceLinkDescription";
    private static final String COMPARISON_GROUP_HEADER = "ComparisonGroup";
    private static final String LINK_ID_HEADER = "LinkId";

    private AggregationHandler aggregationHandler;

    public EmailAggregationStrategy(AggregationHandler aggregationHandler) {
        this.aggregationHandler = aggregationHandler;
    }

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
     *
     * @param oldExchange A {@link Exchange to the previous exchange} which will be aggregated to a final result.
     * @param newExchange A {@link Exchange to the next exchange} which will be aggregated to final result.
     * @return A {@link Exchange} representing the aggregated results of both exchanges.
     */
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Optional<String> linkReference = header(newExchange, LINK_REFERENCE_HEADER);
        if (! linkReference.isPresent()) {
            throw new IllegalArgumentException("Mandatory header not present: ResourceLinkReference");
        };
        //on first invocation, oldExchange will be null as this is the first message that we are aggregating
        ResourceLinksWrapper downloadLinks = createOrGetResourceLinks(oldExchange);
        newExchange.getIn().setHeader(RESOURCE_LINKS_HEADER, downloadLinks);

        Optional<String> linkId = header(newExchange, LINK_ID_HEADER);
        if (! linkId.isPresent()) {
            throw new IllegalArgumentException("Mandatory header not present: LinkId");
        }

        Optional<String> comparisonGroup = header(newExchange, COMPARISON_GROUP_HEADER);
        if (! comparisonGroup.isPresent()) {
            throw new IllegalArgumentException("Mandatory header not present: ComparisonGroup");
        }

        ComparisonGroupModel comparisonGroupModel = aggregationHandler.getAggregationConfiguration(comparisonGroup.get());
        if (comparisonGroupModel == null) {
            throw new IllegalArgumentException("Mandatory configuration not present ComparisonGroupModel: " + comparisonGroup.get());
        }

        EmailLinkModel emailLinkModel = comparisonGroupModel.getEmailLinkModel().get(linkId.get());
        if (emailLinkModel == null) {
            throw new IllegalArgumentException("Mandatory configuration not present EmailLinkModel: " + linkId.get());
        }

        downloadLinks.addDownloadLink(emailLinkModel.getRank(), linkReference.get(), header(newExchange, LINK_DESCRIPTION_HEADER).orElse(null));

        return newExchange;
    }

    private ResourceLinksWrapper createOrGetResourceLinks(Exchange exchange) {
        ResourceLinksWrapper resourceLinks;

        if (exchange == null || (resourceLinks = exchange.getIn().getHeader(RESOURCE_LINKS_HEADER, ResourceLinksWrapper.class)) == null) {
            resourceLinks = new ResourceLinksWrapper(new TreeSet<ResourceLink>(Comparator.comparing(ResourceLink::getRank)));
        }

        return resourceLinks;
    }

    private Optional<String> header(Exchange exchange, String headerName) {
        return Optional.ofNullable(exchange.getIn().getHeader(headerName, String.class));
    }
}