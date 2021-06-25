package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Transforms an SQL ResultSet into a {@link uk.gov.companieshouse.reconciliation.model.InsolvencyResults results object}
 * that can be compared with insolvency details fetched from another data source.<br>
 * <br>
 * IN:<br>
 * body(): {@link java.util.List list} of {@link java.util.Map map} representing company numbers and insolvency details
 * fetched from Oracle.<br>
 * <br>
 * OUT:<br>
 * body(): An {@link uk.gov.companieshouse.reconciliation.model.InsolvencyResults object} aggregating insolvency data
 * fetched from Oracle.<br>
 */
@Component
public class OracleInsolvencyCasesTransformerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:oracle-insolvency-cases")
                .bean(OracleInsolvencyCasesTransformer.class);
    }
}
