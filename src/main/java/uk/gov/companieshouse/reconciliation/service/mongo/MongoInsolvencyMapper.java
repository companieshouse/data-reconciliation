package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.builder.RouteBuilder;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Retrieve insolvencies from MongoDB and transform results into a
 * {@link uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList resource list}.<br>
 * <br>
 * IN:<br>
 * <br>
 * header(Description): A description of the results produced by this pipeline.<br>
 * <br>
 * OUT:<br>
 * <br>
 * body(): Company numbers fetched from the insolvency collection in MongoDB.<br>
 */
@Component
public class MongoInsolvencyMapper extends RouteBuilder {

    @Autowired
    private List<Bson> insolvencyAggregationQuery;

    @Override
    public void configure() throws Exception {
        from("direct:mongo-insolvency-mapper")
                .setHeader("MongoEndpoint", constant("{{endpoint.mongodb.insolvency_collection}}"))
                .setHeader("MongoCacheKey", constant("{{endpoint.mongodb.insolvency.cache.key}}"))
                .setHeader("MongoTransformer", constant("{{transformer.mongo.insolvency_cases}}"))
                .setHeader("MongoQuery", constant(insolvencyAggregationQuery))
                .enrich()
                .constant("{{endpoint.mongodb.wrapper.aggregation.collection}}")
                .aggregationStrategy((prev, curr) -> {
                    prev.getIn().setBody(curr.getIn().getBody());
                    prev.getIn().setHeader("Failed", curr.getIn().getHeader("Failed"));
                    return prev;
                })
                .choice()
                .when(header("Failed").isNotEqualTo(true))
                    .bean(MongoInsolvencyResultsToResourceListTransformer.class)
                .end();
    }
}
