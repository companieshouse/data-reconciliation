package uk.gov.companieshouse.reconciliation.service.mongo;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Retrieve company profiles from MongoDB and transform results into a {@link uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList resource list}.<br>
 * <br>
 * IN:<br>
 * <br>
 * header(Description): A description of the results produced by this pipeline.<br>
 * <br>
 * OUT:<br>
 * <br>
 * body(): Company numbers of company profiles fetched from MongoDB.<br>
 */
@Component
public class MongoCompanyNumberMapper extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:mongo-company_number-mapper")
                .setHeader("MongoCacheKey").constant("{{endpoint.mongodb.company_profile.cache.key}}")
                .setHeader("MongoQuery").constant(Collections.singletonList(Aggregates.project(Projections.include("_id", "data.company_name", "data.company_status"))))
                .setHeader("MongoEndpoint").constant("{{endpoint.mongodb.company_profile_collection}}")
                .setHeader("MongoTransformer").constant("TODO") //TODO: Extract transformer into separate route
                .to("{{endpoint.mongodb.wrapper.aggregation.collection}}")
                .choice()
                .when(header("Failed").isNotEqualTo(true))
                    .bean(MongoCompanyNumberTransformer.class)
                .end();
    }
}
