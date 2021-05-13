package uk.gov.companieshouse.reconciliation.function.compare_collection;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
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
                .enrich()
                .simple("${header.Src}")
                .enrich()
                .simple("${header.Target}")
                .bean(CompareCollectionTransformer.class)
                .marshal().csv()
                .toD("${header.Upload}")
                .toD("${header.Presign}")
                .setHeader("CompareCollectionLink", body())
                .choice()
                .when(header("ElasticsearchDescription"))
                    .setHeader("CompareCollectionDescription", simple("Comparisons completed for ${header.Comparison} in ${header.MongoDescription} and ${header.ElasticsearchDescription}."))
                .when(header("OracleDescription"))
                    .setHeader("CompareCollectionDescription", simple("Comparisons completed for ${header.Comparison} in ${header.MongoDescription} and ${header.OracleDescription}."))
                .end()
                .log("Compare Collection: ${header.CompareCollectionDescription}")
                .toD("${header.Destination}");
    }
}
