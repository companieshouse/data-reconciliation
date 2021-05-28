package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.builder.AggregationStrategies;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.language.xpath.XPathBuilder;
import org.springframework.stereotype.Component;

/**
 * Retrieves and aggregates multiple ResultSets from Oracle.<br>
 * <br>
 * IN:<br>
 * header(OracleQuery): The queries that will be run against the Oracle database.<br>
 * header(OracleEndpoint): The endpoint representing the Oracle database that will be connected to.<br>
 */
@Component
public class OracleMultiCollectionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:oracle-multi-collection")
                .setBody(header("OracleQuery"))
                .split(xpath("/sql-statements/sql-statement"), AggregationStrategies.groupedExchange())
                .setHeader("CompanyStatus", xpath("/sql-statement/@status"))
                .setBody(xpath("/sql-statement/text()"))
                .toD("${header.OracleEndpoint}")
                .end()
                .bean(OracleCompanyStatusTransformer.class);
    }
}
