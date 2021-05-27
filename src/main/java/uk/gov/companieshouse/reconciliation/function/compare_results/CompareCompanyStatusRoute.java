package uk.gov.companieshouse.reconciliation.function.compare_results;

import org.apache.camel.builder.RouteBuilder;
import uk.gov.companieshouse.reconciliation.function.compare_results.transformer.CompareCompanyStatusTransformer;

public class CompareCompanyStatusRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:compare-company-statuses")
                .bean(CompareCompanyStatusTransformer.class);
    }
}
