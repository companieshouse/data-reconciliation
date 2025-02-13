package uk.gov.companieshouse.reconciliation.function.compare_collection;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.ComparisonFailedException;
import uk.gov.companieshouse.reconciliation.function.compare_collection.transformer.CompareCollectionTransformer;

/**
 * Compare lists of resources from two endpoints with each other.<br>
 * <br>
 * IN:<br>
 * <br>
 * header(Src): The first endpoint from which a list of resources will be obtained.<br>
 * header(Target): The second endpoint from which a list of resources will be obtained.<br>
 * header(Destination): The endpoint to which results will be sent.<br>
 * Upload: The endpoint which will be used to store CSV files into the S3 bucket.<br>
 * Presign: The endpoint which will be used to download CSV files from specific S3 bucket.<br>
 * <br>
 * OUT:<br>
 * <br>
 * body(): CSV tabulating which resources are exclusive to each endpoint.
 */
@Component
public class CompareCollectionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:compare_collection")
                .onException(ComparisonFailedException.class)
                    .setHeader("ResourceLinkDescription", simple("Failed to perform ${header.ComparisonDescription}"))
                    .setHeader("Failed").constant(true)
                    .log(LoggingLevel.ERROR, "${header.ResourceLinkDescription}")
                    .handled(true)
                    .toD("${header.Destination}")
                .end()
                .setHeader("Description").header("SrcDescription")
                .enrich()
                .simple("${header.Src}")
                .aggregationStrategy((prev, curr) -> {
                    if(curr.getIn().getHeader("Failed", boolean.class)) {
                        throw new ComparisonFailedException("Failed");
                    }
                    prev.getIn().setHeader("SrcList", curr.getIn().getBody());
                    return prev;
                })
                .setHeader("Description").header("TargetDescription")
                .enrich()
                .simple("${header.Target}")
                .aggregationStrategy((prev, curr) -> {
                    if(curr.getIn().getHeader("Failed", boolean.class)) {
                        throw new ComparisonFailedException("Failed");
                    }
                    prev.getIn().setHeader("TargetList", curr.getIn().getBody());
                    return prev;
                })
                .bean(CompareCollectionTransformer.class)
                .removeHeader("SrcList")
                .removeHeader("TargetList")
                .marshal().csv()
                .setHeader("ResourceLinkDescription", simple("Completed ${header.ComparisonDescription}"))
                .log("${header.ResourceLinkDescription}")
                .toD("${header.Destination}");
    }
}
