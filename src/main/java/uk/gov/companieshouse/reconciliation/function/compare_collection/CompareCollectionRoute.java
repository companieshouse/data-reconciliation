package uk.gov.companieshouse.reconciliation.function.compare_collection;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.transformer.CompareCollectionTransformer;

/**
 * Compare lists of resources from two endpoints with each other.
 *
 * The following request headers should be set when a message is sent to this route:
 *
 * Src: The first endpoint from which a list of resources will be obtained.
 * Target: The second endpoint from which a list of resources will be obtained.
 * Destination: The endpoint to which results will be sent.
 *
 * The response body will tabulate which resources are exclusive to each endpoint.
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
                .log("${body}")
                .toD("${header.Destination}");
    }
}
