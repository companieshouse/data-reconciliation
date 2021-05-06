package uk.gov.companieshouse.reconciliation.function.email.aggregator;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
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
    private static final String COMPARE_COUNT_BODY = "CompareCountBody";
    private static final String COMPARE_COLLECTION_BODY = "CompareCollectionBody";
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
            return newExchange;
        }
        else {
            assignHeaders(oldExchange, newExchange);

            String companyCountHeader = newExchange.getIn().getHeader(COMPANY_COUNT, String.class);
            String companyCollectionHeader = newExchange.getIn().getHeader(COMPANY_COLLECTION , String.class);
            String compareCountBody = newExchange.getIn().getHeader(COMPARE_COUNT_BODY, String.class);
            String compareCollectionBody = newExchange.getIn().getHeader(COMPARE_COLLECTION_BODY, String.class);

            if (companyCountHeader != null && companyCollectionHeader != null) {
                newExchange.getIn().setBody(compareCountBody + CHARACTER_DELIMITER + compareCollectionBody);
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

        Optional.ofNullable(oldExchange.getIn().getHeader(COMPARE_COUNT_BODY, String.class))
                .ifPresent(header -> newExchange.getIn().setHeader(COMPARE_COUNT_BODY, header));

        Optional.ofNullable(oldExchange.getIn().getHeader(COMPARE_COLLECTION_BODY, String.class))
                .ifPresent(header -> newExchange.getIn().setHeader(COMPARE_COLLECTION_BODY, header));
    }
}