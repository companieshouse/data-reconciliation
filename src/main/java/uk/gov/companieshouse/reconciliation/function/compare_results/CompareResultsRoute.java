package uk.gov.companieshouse.reconciliation.function.compare_results;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_results.transformer.CompareResultsTransformer;

@Component
public class CompareResultsRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:compare_results")
                .enrich()
                .simple("${header.Src}")
                .aggregationStrategy((oldExchange, newExchange) -> {
                    oldExchange.getIn().setHeader("SrcList", newExchange.getIn().getBody());
                    return oldExchange;
                })
                .enrich()
                .simple("${header.Target}")
                .aggregationStrategy((oldExchange, newExchange) -> {
                    oldExchange.getIn().setHeader("TargetList", newExchange.getIn().getBody());
                    return oldExchange;
                })
                .bean(CompareResultsTransformer.class)
                .marshal().csv()
                .log("Compare Results: ${header.ResourceLinkDescription}")
                .toD("${header.Destination}");
    }
}
