package uk.gov.companieshouse.reconciliation.service.mongo;

import com.mongodb.MongoException;
import org.apache.camel.LoggingLevel;
import org.apache.camel.component.caffeine.CaffeineConstants;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.common.RetryableRoute;

/**
 * Retrieves and aggregates company profiles from MongoDB.<br>
 * <br>
 * IN:<br>
 * header(MongoQuery): The query that will be sent to MongoDB.<br>
 * header(MongoCacheKey): The cache key underneath which results will be stored.<br>
 * header(MongoEndpoint): The MongoDB endpoint that will be used to fetch data from MongoDB.<br>
 * header(MongoTransformer): The transformer that will be used to transform data fetched from MongoDB into an appropriate
 * data structure.<br>
 * <br>
 * OUT:<br>
 * <br>
 * body(): results fetched from the collection.<br>
 */
@Component
public class MongoAggregationRoute extends RetryableRoute {

    @Override
    public void configure() {
        super.configure();
        from("direct:mongodb-aggregation-collection")
                .onException(MongoException.class)
                    .handled(false)
                    .log(LoggingLevel.ERROR, "Failed to retrieve data from MongoDB")
                    .setHeader("Failed").constant(true)
                .end()
                .setHeader(CaffeineConstants.ACTION).constant(CaffeineConstants.ACTION_GET)
                .setHeader(CaffeineConstants.KEY).simple("${header.MongoCacheKey}")
                .to("{{endpoint.cache}}")
                .choice()
                .when(header(CaffeineConstants.ACTION_HAS_RESULT).isEqualTo(false))
                    .setBody().simple("${header.MongoQuery}")
                    .toD("${header.MongoEndpoint}")
                    .log("${body.size()} results have been fetched from mongodb.")
                    .toD("${header.MongoTransformer}")
                    .setHeader(CaffeineConstants.ACTION).constant(CaffeineConstants.ACTION_PUT)
                    .to("{{endpoint.cache}}")
                .otherwise()
                    .log("${body.size()} results have been fetched from the cache.")
                .end();
    }
}
