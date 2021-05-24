package uk.gov.companieshouse.reconciliation.function.compare_results;

import org.apache.camel.builder.RouteBuilder;
import uk.gov.companieshouse.reconciliation.function.compare_results.transformer.CompareResultsTransformer;

public class CompareResultsRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:compare_results")
                .enrich()
                .simple("${header.Src}")
                .enrich()
                .simple("${header.Target}")
                .bean(CompareResultsTransformer.class);
    }
}
