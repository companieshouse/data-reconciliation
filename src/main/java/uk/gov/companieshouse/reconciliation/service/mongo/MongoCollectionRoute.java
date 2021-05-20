package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

/**
 * Retrieves and aggregates results from a mongo.collection.distinct query.<br>
 * <br>
 * IN:<br>
 * <br>
 * header(MongoEndpoint): The endpoint from which results will be fetched.<br>
 * header(MongoDescription): A description of the {@link ResourceList resource list} that will be aggregated.<br>
 * header(MongoTargetHeader): The header in which results will be aggregated as a {@link ResourceList resource list}.<br>
 */
@Component
public class MongoCollectionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:mongodb-collection")
                .toD("${header.MongoEndpoint}")
                .bean(MongoAggregationTransformer.class);
    }
}
