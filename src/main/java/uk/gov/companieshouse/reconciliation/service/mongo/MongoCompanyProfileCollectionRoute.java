package uk.gov.companieshouse.reconciliation.service.mongo;

import com.mongodb.MongoException;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import org.apache.camel.LoggingLevel;
import org.apache.camel.component.caffeine.CaffeineConstants;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.common.RetryableRoute;

import java.util.Collections;

/**
 * Retrieves and aggregates company profiles from MongoDB.<br>
 * <br>
 * OUT:<br>
 * <br>
 * body(): {@link uk.gov.companieshouse.reconciliation.model.Results Company profiles} fetched from the collection.<br>
 */
@Component
public class MongoCompanyProfileCollectionRoute extends RetryableRoute {

    @Override
    public void configure() {
        super.configure();
        from("direct:mongodb-company_profile-collection")
                .onException(MongoException.class)
                    .handled(true)
                    .log(LoggingLevel.ERROR, "Failed to retrieve company profile data from MongoDB")
                    .setHeader("Failed").constant(true)
                .end()
                .setHeader(CaffeineConstants.ACTION).constant(CaffeineConstants.ACTION_GET)
                .setHeader(CaffeineConstants.KEY).constant("{{endpoint.mongodb.company_profile.cache.key}}")
                .to("{{endpoint.cache}}")
                .choice()
                .when(header(CaffeineConstants.ACTION_HAS_RESULT).isEqualTo(false))
                    .setBody().constant(Collections.singletonList(Aggregates.project(Projections.include("_id", "data.company_name", "data.company_status"))))
                    .to("{{endpoint.mongodb.company_profile_collection}}")
                    .log("${body.size()} results have been fetched from mongodb.")
                    .bean(MongoAggregationTransformer.class)
                    .setHeader(CaffeineConstants.ACTION).constant(CaffeineConstants.ACTION_PUT)
                    .to("{{endpoint.cache}}")
                .otherwise()
                    .log("${body.size()} results have been fetched from the cache.")
                .end();
    }
}