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
                    .setHeader("ResourceLinkDescription", simple("Comparison failed for ${header.Comparison} in ${header.SrcDescription} and ${header.TargetDescription}."))
                    .log(LoggingLevel.ERROR, "${header.ResourceLinkDescription}")
                    .handled(true)
                    .toD("${header.Destination}")
                .end()
                .toD("${header.Src}")
                .choice()
                .when(header("Failed").isEqualTo(true))
                    .throwException(ComparisonFailedException.class, "Comparison source failed")
                .end()
                .toD("${header.Target}")
                .choice()
                .when(header("Failed").isEqualTo(true))
                    .throwException(ComparisonFailedException.class, "Comparison target failed")
                .end()
                .bean(CompareCollectionTransformer.class)
                .marshal().csv()
                .toD("${header.Upload}")
                .toD("${header.Presign}")
                .setHeader("ResourceLinkReference", body())
                .setHeader("ResourceLinkDescription", simple("Comparisons completed for ${header.Comparison} in ${header.SrcDescription} and ${header.TargetDescription}."))
                .log("${header.ResourceLinkDescription}")
                .toD("${header.Destination}");
    }
}
