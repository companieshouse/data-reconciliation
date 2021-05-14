package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.springframework.stereotype.Component;

/**
 * Trigger a comparison between company profiles in MongoDB and company profiles that have been indexed in the
 * Elasticsearch primary index.
 */
@Component
public class CompanyNumberCompareMongoDBPrimarySearch extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.company_collection_mongo_primary.cron.tab}}")
                .setHeader("MongoEndpoint", simple("{{endpoint.mongodb.company_profile_collection}}"))
                .setHeader("MongoDescription", constant("MongoDB"))
                .setHeader("MongoTargetHeader", constant("SrcList"))
                .setHeader(MongoDbConstants.DISTINCT_QUERY_FIELD, constant("_id"))
                .setHeader("Src", simple("{{endpoint.mongodb.collection}}"))
                .setHeader("ElasticsearchEndpoint", simple("{{endpoint.elasticsearch.primary}}"))
                .setHeader("ElasticsearchQuery", simple("{{query.elasticsearch.primary.company}}"))
                .setHeader("ElasticsearchDescription", constant("Primary Index"))
                .setHeader("ElasticsearchTargetHeader", constant("TargetList"))
                .setHeader("ElasticsearchLogIndices", simple("{{endpoint.elasticsearch.log_indices}}"))
                .setHeader("Target", simple("{{endpoint.elasticsearch.collection}}"))
                .setHeader("Comparison", simple("primary search index"))
                .setHeader("Destination", simple("{{endpoint.log.output}}"))
                .to("{{function.name.compare_collection}}");
    }
}
