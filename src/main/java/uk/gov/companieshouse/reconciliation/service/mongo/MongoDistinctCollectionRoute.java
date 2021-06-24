package uk.gov.companieshouse.reconciliation.service.mongo;

import com.mongodb.MongoException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.caffeine.CaffeineConstants;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

/**
 * Retrieves and aggregates disqualifications from MongoDB.<br>
 * <br>
 * IN:<br>
 * <br>
 * header(Description): A description of the {@link ResourceList resource list} where results will be aggregated.<br>
 * header(MongoDistinctEndpoint): A Mongo DB endpoint from which results will be fetched.<br>
 * header(MongoDistinctCacheKey): The cache key underneath which results will be stored.<br>
 * header(CamelMongoDbDistinctQueryField): The field for which to return distinct values.<br>
 * header(CamelMongoDbCriteria): An optional field used to filter documents that are returned.<br>
 */
@Component
public class MongoDistinctCollectionRoute extends RouteBuilder {

    @Override
    public void configure() {
        from("direct:mongodb-distinct-collection")
                .onException(MongoException.class)
                    .handled(true)
                    .log(LoggingLevel.ERROR, "Failed to retrieve collection data from MongoDB")
                    .setHeader("Failed").constant(true)
                .end()
                .setHeader(CaffeineConstants.ACTION).constant(CaffeineConstants.ACTION_GET)
                .setHeader(CaffeineConstants.KEY).header("MongoDistinctCacheKey")
                .to("{{endpoint.cache}}")
                .choice()
                .when(header(CaffeineConstants.ACTION_HAS_RESULT).isEqualTo(false))
                    .setBody(header("MongoQuery"))
                    .toD("${header.MongoDistinctEndpoint}")
                    .log("${body.size()} items have been fetched from mongodb.")
                    .setHeader(CaffeineConstants.ACTION).constant(CaffeineConstants.ACTION_PUT)
                    .to("{{endpoint.cache}}")
                .otherwise()
                    .log("${body.size()} disqualifications have been fetched from the cache.")
                .end()
                .bean(MongoDistinctToResourceListTransformer.class);
    }
}
