package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Transforms an SQL ResultSet containing a single column into a
 * {@link uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList resource list}
 * that can be compared with resources fetched from another data source.<br>
 * <br>
 * IN:<br>
 * body(): {@link java.util.List list} of {@link java.util.Map map} representing details fetched from Oracle.<br>
 * <br>
 * OUT:<br>
 * body(): A {@link uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList resource list}
 * aggregating data fetched from Oracle.<br>
 */
@Component
public class OracleSingleColumnTransformerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:oracle-single-column")
                .bean(OracleResultCollectionTransformer.class);
    }
}
