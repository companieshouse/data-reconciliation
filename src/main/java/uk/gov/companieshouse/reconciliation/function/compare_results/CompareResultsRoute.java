package uk.gov.companieshouse.reconciliation.function.compare_results;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Compare resource data from two endpoints with each other.<br>
 * <br>
 * IN:<br>
 * <br>
 * header(Src): The first endpoint from which a list of resources will be obtained.<br>
 * header(SrcDescription): Description of the first endpoint.<br>
 * header(Target): The second endpoint from which a list of resources will be obtained.<br>
 * header(TargetDescription): Description of the second endpoint.<br>
 * header(ResourceType): The type of resource being compared.
 * header(Destination): The endpoint to which results will be sent.<br>
 * <br>
 * OUT:<br>
 * <br>
 * body(): CSV tabulating which resources are exclusive to each endpoint.
 */
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
                .toD("${header.ResultsTransformer}")
                .marshal().csv()
                .toD("${header.Upload}")
                .toD("${header.Presign}")
                .setHeader("ResourceLinkReference", body())
                .setHeader("ResourceLinkDescription").simple("Comparisons completed for ${header.Comparison} in ${header.SrcDescription} and ${header.TargetDescription}.")
                .log("Compare Results: ${header.ResourceLinkDescription}")
                .toD("${header.Destination}");
    }
}
