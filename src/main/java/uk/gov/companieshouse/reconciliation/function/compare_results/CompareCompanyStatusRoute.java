package uk.gov.companieshouse.reconciliation.function.compare_results;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_results.transformer.CompareCompanyStatusTransformer;

@Component
public class CompareCompanyStatusRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:compare-company-status")
                .bean(CompareCompanyStatusTransformer.class);
    }
}
