package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class OracleSingleColumnTransformerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:oracle-single-column")
                .bean(OracleResultCollectionTransformer.class);
    }
}
