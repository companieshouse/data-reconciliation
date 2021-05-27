package uk.gov.companieshouse.reconciliation.function.compare_results;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_results.transformer.CompareCompanyNamesTransformer;

@Component
public class CompareCompanyNameRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:compare-company-names")
                .bean(CompareCompanyNamesTransformer.class);
    }
}
