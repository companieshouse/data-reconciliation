package uk.gov.companieshouse.reconciliation.function.email;

import org.apache.camel.Exchange;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sorts {@link List<PublisherResourceRequest> a list of publisher source requests} into an natural order
 * based of the assigned orderNumber header.
 * **/
public class EmailLinksSorter {

    private static final String ELASTICSEARCH_GROUP = "Elasticsearch";
    private static final String COMPANY_PROFILE_GROUP = "Company profile";

    public void map(Exchange exchange) {
        String comparisonGroup = exchange.getIn().getHeader("ComparisonGroup", String.class);
        PublisherResourceRequestWrapper srcPublisherResourceRequestWrapper = (PublisherResourceRequestWrapper) exchange.getIn().getHeaders().get("PublisherResourceRequests");
        List<PublisherResourceRequest> srcPublisherRequests = srcPublisherResourceRequestWrapper.getRequests();

        if (ELASTICSEARCH_GROUP.equals(comparisonGroup) || COMPANY_PROFILE_GROUP.equals(comparisonGroup)) {
            List<PublisherResourceRequest> sortedPublisherRequests = srcPublisherRequests.stream()
                    .sorted(Comparator.comparingInt(PublisherResourceRequest::getOrderNumber))
                    .collect(Collectors.toList());
            PublisherResourceRequestWrapper sortedPublisherResourceRequestWrapper = new PublisherResourceRequestWrapper(sortedPublisherRequests);
            exchange.getIn().setHeader("PublisherResourceRequests", sortedPublisherResourceRequestWrapper);
        }
    }
}
