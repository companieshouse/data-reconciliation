package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.builder.RouteBuilder;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Retrieve disqualified officers from MongoDB and transform results into a
 * {@link uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList resource list}.<br>
 * <br>
 * IN:<br>
 * <br>
 * header(Description): A description of the results produced by this pipeline.<br>
 * <br>
 * OUT:<br>
 * <br>
 * body(): Oracle IDs of disqualified officers fetched from MongoDB.<br>
 */
@Component
public class MongoDisqualifiedOfficerMapper extends RouteBuilder {

    @Autowired
    private List<Bson> disqualifiedOfficerAggregationQuery;

    @Override
    public void configure() throws Exception {
        from("direct:mongo-disqualified_officer-mapper")
                .setHeader("MongoEndpoint", constant("{{endpoint.mongodb.disqualifications_collection}}"))
                .setHeader("MongoCacheKey", constant("{{endpoint.mongodb.disqualifications.cache.key}}"))
                .setHeader("MongoTransformer", constant("{{transformer.mongo.disqualified_officer}}"))
                .setHeader("MongoQuery", constant(disqualifiedOfficerAggregationQuery))
                .enrich()
                .constant("{{endpoint.mongodb.wrapper.aggregation.collection}}")
                .aggregationStrategy((prev, curr) -> {
                    prev.getIn().setBody(curr.getIn().getBody());
                    prev.getIn().setHeader("Failed", curr.getIn().getHeader("Failed"));
                    return prev;
                })
                .choice()
                .when(header("Failed").isNotEqualTo(true))
                    .bean(MongoDisqualifiedOfficerResultsToResourceListTransformer.class)
                .end();
    }
}
