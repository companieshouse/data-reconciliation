package uk.gov.companieshouse.reconciliation.service.mongo;

import com.mongodb.MongoException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.caffeine.CaffeineConstants;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

/**
 * Retrieves and aggregates disqualifications from MongoDB.<br>
 * <br>
 * IN:<br>
 * <br>
 * header(Description): A description of the {@link ResourceList resource list} where results will be aggregated.<br>
 * header(MongoTargetHeader): The header where results will be aggregated as a {@link ResourceList resource list}.<br>
 */
@Component
public class MongoDisqualificationsCollectionRoute extends RouteBuilder {

    @Value("${wrappers.retries}")
    private int retries;

    @Override
    public void configure() throws Exception {
        from("direct:mongodb-disqualifications-collection")
                .errorHandler(defaultErrorHandler().maximumRedeliveries(retries))
                    .onException(MongoException.class)
                    .handled(true)
                    .log(LoggingLevel.ERROR, "Failed to retrieve disqualification data from MongoDB")
                    .setHeader("Failed").constant(true)
                .end()
                .setHeader(CaffeineConstants.ACTION).constant(CaffeineConstants.ACTION_GET)
                .setHeader(CaffeineConstants.KEY).constant("{{endpoint.mongodb.disqualifications.cache.key}}")
                .to("{{endpoint.cache}}")
                .choice()
                .when(header(CaffeineConstants.ACTION_HAS_RESULT).isEqualTo(false))
                    .setHeader(MongoDbConstants.DISTINCT_QUERY_FIELD).constant("officer_id_raw")
                    .to("{{endpoint.mongodb.disqualifications_collection}}")
                    .log("${body.size()} disqualifications have been fetched from mongodb.")
                    .setHeader(CaffeineConstants.ACTION).constant(CaffeineConstants.ACTION_PUT)
                    .to("{{endpoint.cache}}")
                .otherwise()
                    .log("${body.size()} disqualifications have been fetched from the cache.")
                .end()
                .bean(MongoDistinctToResourceListTransformer.class);
    }
}
