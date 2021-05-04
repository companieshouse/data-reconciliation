package uk.gov.companieshouse.reconciliation.function.email.aggregator;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import java.util.Optional;

public class EmailAggregationStrategy implements AggregationStrategy {

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