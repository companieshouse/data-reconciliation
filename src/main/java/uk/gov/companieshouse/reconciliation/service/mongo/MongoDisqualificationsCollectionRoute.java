package uk.gov.companieshouse.reconciliation.service.mongo;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.caffeine.CaffeineConstants;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.Collections;

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
public class MongoDisqualificationsCollectionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:mongodb-disqualifications-collection")
                .setHeader(CaffeineConstants.ACTION).constant(CaffeineConstants.ACTION_GET)
                .setHeader(CaffeineConstants.KEY).constant("{{endpoint.mongodb.disqualifications.cache.key}}")
                .to("{{endpoint.cache}}")
                .choice()
                .when(header(CaffeineConstants.ACTION_HAS_RESULT).isEqualTo(false))
                    .setHeader(MongoDbConstants.DISTINCT_QUERY_FIELD).constant("officer_id_raw")
                    .toD("${header.MongoEndpoint}")
                    .log("${body.size()} disqualifications have been fetched from mongodb.")
                    .setHeader(CaffeineConstants.ACTION).constant(CaffeineConstants.ACTION_PUT)
                    .to("{{endpoint.cache}}")
                .otherwise()
                    .log("${body.size()} disqualifications have been fetched from the cache.")
                .end()
                .bean(MongoDistinctToResourceListTransformer.class);
    }
}
