package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class OracleInsolvencyCasesTransformerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:oracle-insolvency-cases")
                .bean(OracleInsolvencyCasesTransformer.class);
    }
}
