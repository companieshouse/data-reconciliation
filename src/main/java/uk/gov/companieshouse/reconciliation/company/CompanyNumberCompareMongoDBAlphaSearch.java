package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.springframework.stereotype.Component;

/**
 * Trigger a comparison between company profiles in MongoDB and company profiles that have been indexed in the
 * Elasticsearch alphabetical search index.
 */
@Component
public class CompanyNumberCompareMongoDBAlphaSearch extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.company_collection_mongo_alpha.cron.tab}}")
                .setHeader("MongoEndpoint", simple("{{endpoint.mongodb.company_profile_collection}}"))
                .setHeader("MongoDescription", constant("MongoDB"))
                .setHeader("MongoTargetHeader", constant("SrcList"))
                .setHeader(MongoDbConstants.DISTINCT_QUERY_FIELD, constant("_id"))
                .setHeader("Src", simple("{{endpoint.mongodb.collection}}"))
                .setHeader("ElasticsearchEndpoint", simple("{{endpoint.elasticsearch.alpha}}"))
                .setHeader("ElasticsearchQuery", simple("{{query.elasticsearch.alpha.company}}"))
                .setHeader("ElasticsearchDescription", constant("Alpha Index"))
                .setHeader("ElasticsearchTargetHeader", constant("TargetList"))
                .setHeader("ElasticsearchLogIndices", simple("{{endpoint.elasticsearch.log_indices}}"))
                .setHeader("Target", simple("{{endpoint.elasticsearch.collection}}"))
                .setHeader("Destination", simple("{{endpoint.output}}"))
                .to("{{function.name.compare_collection}}");
    }
}
