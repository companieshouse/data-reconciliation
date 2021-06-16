package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.sql.SQLException;

/**
 * Retrieves and aggregates a ResultSet from Oracle.<br>
 * <br>
 * IN:<br>
 * header(OracleQuery): The query that will be run against the Oracle database.<br>
 * header(OracleEndpoint): The endpoint representing the Oracle database that will be connected to.<br>
 * header(OracleDescription): A description of the {@link ResourceList resource list} that will be aggregated.<br>
 * header(OracleTargetHeader): The header in which results will be aggregated as a {@link ResourceList resource list}.<br>
 */
@Component
public class OracleCollectionRoute extends RouteBuilder {

    @Value("${wrappers.retries}")
    private int retries;

    @Override
    public void configure() throws Exception {
        from("direct:oracle-collection")
                .errorHandler(defaultErrorHandler().maximumRedeliveries(retries))
                    .onException(SQLException.class)
                    .handled(true)
                    .log(LoggingLevel.ERROR, "Failed to retrieve results from Oracle")
                    .setHeader("Failed").constant(true)
                .end()
                .setBody(header("OracleQuery"))
                .toD("${header.OracleEndpoint}")
                .bean(OracleResultCollectionTransformer.class);
    }
}
