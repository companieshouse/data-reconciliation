package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.LoggingLevel;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.common.RetryableRoute;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.sql.SQLException;

/**
 * Retrieves and aggregates a ResultSet from Oracle.<br>
 * <br>
 * IN:<br>
 * header(OracleQuery): The query that will be run against the Oracle database.<br>
 * header(OracleEndpoint): The endpoint representing the Oracle database that will be connected to.<br>
 * header(Description): A description of the {@link ResourceList resource list} that will be aggregated.<br>
 */
@Component
public class OracleCollectionRoute extends RetryableRoute {

    @Override
    public void configure() {
        super.configure();
        from("direct:oracle-collection")
                .onException(SQLException.class)
                    .handled(true)
                    .log(LoggingLevel.ERROR, "Failed to retrieve results from Oracle")
                    .setHeader("Failed").constant(true)
                .end()
                .setBody(header("OracleQuery"))
                .toD("${header.OracleEndpoint}")
                .toD("${header.OracleTransformer}");
    }
}
