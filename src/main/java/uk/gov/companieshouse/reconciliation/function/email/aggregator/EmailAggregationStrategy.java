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

            String companyCountHeader = newExchange.getIn().getHeader("CompanyCount", String.class);
            String companyCollectionHeader = newExchange.getIn().getHeader("CompanyCollection", String.class);
            String compareCountBody = newExchange.getIn().getHeader("CompareCountBody", String.class);
            String compareCollectionBody = newExchange.getIn().getHeader("CompareCollectionBody", String.class);

            if (companyCountHeader != null && companyCollectionHeader != null) {
                newExchange.getIn().setBody(compareCountBody + "\n" + compareCollectionBody);
                newExchange.getIn().setHeader("AggregationComplete", "true");
            }

            return newExchange;
        }
    }

    private void assignHeaders(Exchange oldExchange, Exchange newExchange) {
        Optional.ofNullable(oldExchange.getIn().getHeader("CompanyCount", String.class))
                .ifPresent(header -> newExchange.getIn().setHeader("CompanyCount", header));

        Optional.ofNullable(oldExchange.getIn().getHeader("CompanyCollection", String.class))
                .ifPresent(header -> newExchange.getIn().setHeader("CompanyCollection", header));

        Optional.ofNullable(oldExchange.getIn().getHeader("CompareCountBody", String.class))
                .ifPresent(header -> newExchange.getIn().setHeader("CompareCountBody", header));

        Optional.ofNullable(oldExchange.getIn().getHeader("CompareCollectionBody", String.class))
                .ifPresent(header -> newExchange.getIn().setHeader("CompareCollectionBody", header));
    }
}