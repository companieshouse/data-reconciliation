package uk.gov.companieshouse.reconciliation.function.compare_results;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.ComparisonFailedException;

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
                .onException(ComparisonFailedException.class)
                    .setHeader("ResourceLinkDescription").simple("Failed to perform ${header.ComparisonDescription}")
                    .setHeader("Failed").constant(true)
                    .log("Compare results failed: ${header.ResourceLinkDescription}")
                    .handled(false)
                    .toD("${header.Destination}")
                .end()
                .enrich()
                .simple("${header.Src}")
                .aggregationStrategy((oldExchange, newExchange) -> {
                    if(newExchange.getIn().getHeader("Failed", boolean.class)) {
                        throw new ComparisonFailedException("Comparison failed");
                    }
                    oldExchange.getIn().setHeader("SrcList", newExchange.getIn().getBody());
                    return oldExchange;
                })
                .enrich()
                .simple("${header.Target}")
                .aggregationStrategy((oldExchange, newExchange) -> {
                    if(newExchange.getIn().getHeader("Failed", boolean.class)) {
                        throw new ComparisonFailedException("Comparison failed");
                    }
                    oldExchange.getIn().setHeader("TargetList", newExchange.getIn().getBody());
                    return oldExchange;
                })
                .toD("${header.ResultsTransformer}")
                .removeHeader("SrcList")
                .removeHeader("TargetList")
                .marshal().csv()
                .setHeader("ResourceLinkDescription").simple("Completed ${header.ComparisonDescription}")
                .log("Compare results succeeded: ${header.ResourceLinkDescription}")
                .toD("${header.Destination}");
    }
}
