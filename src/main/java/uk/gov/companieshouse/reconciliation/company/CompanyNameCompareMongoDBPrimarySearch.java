package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Trigger a comparison between company profiles in MongoDB and company profiles that have been indexed in the
 * Elasticsearch alphabetical search index. Any differences between company names will be recorded in the results.
 */
@Component
public class CompanyNameCompareMongoDBPrimarySearch extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.company_name_mongo_primary.cron.tab}}")
                .setHeader("Src").constant("{{endpoint.mongodb.wrapper.company_profile.collection}}")
                .setHeader("SrcDescription").constant("MongoDB - Company Profile")
                .setHeader("Target").constant("{{endpoint.elasticsearch.collection}}")
                .setHeader("TargetDescription").constant("Primary Search Index")
                .setHeader("ElasticsearchEndpoint").constant("{{endpoint.elasticsearch.primary}}")
                .setHeader("ElasticsearchQuery").constant("{{query.elasticsearch.primary.company}}")
                .setHeader("ElasticsearchCacheKey").constant("{{endpoint.elasticsearch.primary.cache.key}}")
                .setHeader("RecordType").constant("Company Number")
                .setHeader("Destination").constant("{{endpoint.log.output}}")
                .to("{{function.name.compare_results}}");

    }
}
