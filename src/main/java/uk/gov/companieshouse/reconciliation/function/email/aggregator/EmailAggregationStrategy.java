package uk.gov.companieshouse.reconciliation.function.email.aggregator;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import uk.gov.companieshouse.reconciliation.model.ResourceLink;
import uk.gov.companieshouse.reconciliation.model.ResourceLinksWrapper;

import java.util.ArrayList;
import java.util.Optional;

/**
 * A custom aggregation strategy defined in respects to the Aggregate EIP Pattern.
 * Purpose of the this strategy is to compose the results of different comparison jobs ran
 * to a final output body to be used by respective endpoints.
 */
public class EmailAggregationStrategy implements AggregationStrategy {

    private static final String CHARACTER_DELIMITER = "\n";
    private static final String COMPANY_COUNT  = "CompanyCount";
    private static final String COMPANY_COLLECTION = "CompanyCollection";
    private static final String AGGREGATION_COMPLETE = "AggregationComplete";

    /**
     *
     * This aggregation strategy keeps track of previous headers located inside the exchange
     * which would than be used to determine when the aggregation strategy should complete.
     * This aggregation strategy would also use the previous comparisons bodies that is set inside a header
     * to be composed into a singular output body to be used for respective endpoints.
     *
     * @param oldExchange   A {@link Exchange to the previous exchange} which will be aggregated to a final result.
     * @param newExchange   A {@link Exchange to the next exchange} which will be aggregated to final result.
     * @return A {@link Exchange} representing the aggregated results of the two above exchanges.
     */
    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            ResourceLinksWrapper downloadLinks = new ResourceLinksWrapper(new ArrayList<>());

            newExchange.getIn().setHeader("ResourceLinks", downloadLinks);
            return newExchange;
        }
        else {
            //assignHeaders(oldExchange, newExchange);

            if (oldExchange.getIn().getHeaders().containsKey("ResourceLinks")) {

            }

            if (newExchange.getIn().getHeaders().containsKey("CompareCountLink")) {
                ResourceLink companyCountLink = new ResourceLink(newExchange.getIn().getHeader("CompareCountLink", String.class),
                        newExchange.getIn().getHeader("CompareCountDescription", String.class));
            }




            String companyCountHeader = newExchange.getIn().getHeader(COMPANY_COUNT, String.class);
            String companyCollectionHeader = newExchange.getIn().getHeader(COMPANY_COLLECTION , String.class);

            if (companyCountHeader != null && companyCollectionHeader != null) {
                newExchange.getIn().setHeader(AGGREGATION_COMPLETE, "true");
            }

            return newExchange;
        }
    }

    private void assignHeaders(Exchange oldExchange, Exchange newExchange) {
        Optional.ofNullable(oldExchange.getIn().getHeader(COMPANY_COUNT, String.class))
                .ifPresent(header -> newExchange.getIn().setHeader(COMPANY_COUNT, header));

        Optional.ofNullable(oldExchange.getIn().getHeader(COMPANY_COLLECTION, String.class))
                .ifPresent(header -> newExchange.getIn().setHeader(COMPANY_COLLECTION, header));

        Optional.ofNullable(oldExchange.getIn().getHeader("CompareCountLink", String.class))
                .ifPresent(header -> newExchange.getIn().setHeader("CompareCountLink", header));

        Optional.ofNullable(oldExchange.getIn().getHeader("CompareCountDescription", String.class))
                .ifPresent(header -> newExchange.getIn().setHeader("CompareCountDescription", header));
    }
}