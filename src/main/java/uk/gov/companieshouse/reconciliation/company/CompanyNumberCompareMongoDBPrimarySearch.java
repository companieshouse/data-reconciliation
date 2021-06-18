package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
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
        from("{{endpoint.company_collection_mongo_primary.timer}}")
                .setHeader("SrcDescription", constant("MongoDB"))
                .setHeader(MongoDbConstants.DISTINCT_QUERY_FIELD, constant("_id"))
                .setHeader("Src", simple("{{endpoint.mongodb.mapper.collection.company_number}}"))
                .setHeader("ElasticsearchEndpoint", simple("{{endpoint.elasticsearch.primary}}"))
                .setHeader("ElasticsearchQuery", simple("{{query.elasticsearch.primary.company}}"))
                .setHeader("TargetDescription", constant("Primary Index"))
                .setHeader("ElasticsearchLogIndices", simple("{{endpoint.elasticsearch.log_indices}}"))
                .setHeader("ElasticsearchCacheKey", constant("{{endpoint.elasticsearch.primary.cache.key}}"))
                .setHeader("ElasticsearchTransformer", constant("{{transformer.elasticsearch.primary}}"))
                .setHeader("Target", constant("{{endpoint.elasticsearch.collection.company_number}}"))
                .setHeader("Comparison", constant("company numbers"))
                .setHeader("ComparisonGroup", constant("Elasticsearch"))
                .setHeader("RecordType", constant("Company Number"))
                .setHeader("Destination", simple("{{endpoint.output}}"))
                .setHeader("Upload", simple("{{endpoint.s3.upload}}"))
                .setHeader("Presign", simple("{{endpoint.s3presigner.download}}"))
                .setHeader(AWS2S3Constants.KEY, simple("company/collection_primary_mongo_${date:now:yyyyMMdd}T${date:now:hhmmss}.csv"))
                .setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, simple("{{aws.expiry}}"))
                .to("{{function.name.compare_collection}}");
    }
}
