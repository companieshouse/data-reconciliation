package uk.gov.companieshouse.reconciliation.function.compare_results;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_results.transformer.CompareInsolvencyCasesTransformer;

@Component
public class CompareInsolvencyCasesRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:compare-insolvency-cases")
                .bean(CompareInsolvencyCasesTransformer.class);
    }
}
