package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.GroupedExchangeAggregationStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

/**
 * Retrieves and aggregates multiple ResultSets from Oracle.<br>
 * <br>
 * IN:<br>
 * header(OracleQuery): The queries that will be run against the Oracle database.<br>
 * header(OracleEndpoint): The endpoint representing the Oracle database that will be connected to.<br>
 */
@Component
public class OracleCompanyStatusCollectionRoute extends RouteBuilder {

    @Value("${wrappers.retries}")
    private int retries;

    @Override
    public void configure() throws Exception {
        from("direct:oracle-company-status-collection")
                .errorHandler(defaultErrorHandler().maximumRedeliveries(retries))
                    .onException(SQLException.class)
                    .handled(true)
                    .log(LoggingLevel.ERROR, "Failed to retrieve results from Oracle")
                    .setHeader("Failed").constant(true)
                .end()
                .setBody(header("OracleQuery"))
                .setBody(xpath("/sql-statements/valid-companies-query/sql-statement/text()"))
                .toD("${header.OracleEndpoint}")
                .setHeader("ValidCompanies", body())
                .setBody(header("OracleQuery"))
                .split(xpath("/sql-statements/status-classification-queries/sql-statement"),
                        new GroupedExchangeAggregationStrategy() {
                            @Override
                            public Exchange aggregate(Exchange oldExchange, Exchange newExchange,
                                    Exchange inputExchange) {
                                Exchange aggregation = super.aggregate(oldExchange, newExchange, inputExchange);
                                aggregation.getIn().setHeader("ValidCompanies", inputExchange.getIn().getHeader("ValidCompanies"));
                                return aggregation;
                            }
                        })
                .setHeader("CompanyStatus", xpath("/sql-statement/@status"))
                .setBody(xpath("/sql-statement/text()"))
                .toD("${header.OracleEndpoint}")
                .end()
                .bean(OracleCompanyStatusTransformer.class);
    }
}
