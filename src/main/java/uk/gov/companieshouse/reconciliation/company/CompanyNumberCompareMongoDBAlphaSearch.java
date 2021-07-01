package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;

/**
 * Trigger a comparison between company profiles in MongoDB and company profiles that have been indexed in the
 * Elasticsearch alphabetical search index.
 */
@Component
public class CompanyNumberCompareMongoDBAlphaSearch extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.company_number_mongo_alpha.timer}}")
                .autoStartup("{{company_number_mongo_alpha_enabled}}")
                .setHeader("SrcDescription", constant("MongoDB"))
                .setHeader("Src", constant("{{endpoint.mongodb.mapper.collection.company_number}}"))
                .setHeader("ElasticsearchEndpoint", constant("{{endpoint.elasticsearch.alpha}}"))
                .setHeader("ElasticsearchQuery", constant("{{query.elasticsearch.alpha.company}}"))
                .setHeader("TargetDescription", constant("Alpha Index"))
                .setHeader("ElasticsearchLogIndices", constant("{{endpoint.elasticsearch.log_indices}}"))
                .setHeader("ElasticsearchCacheKey", constant("{{endpoint.elasticsearch.alpha.cache.key}}"))
                .setHeader("ElasticsearchTransformer", constant("{{transformer.elasticsearch.alpha}}"))
                .setHeader("Target", constant("{{endpoint.elasticsearch.collection.company_number}}"))
                .setHeader("ComparisonGroup", constant("Elasticsearch"))
                .setHeader("ComparisonDescription", constant("company number comparison between MongoDB and alpha index"))
                .setHeader("RecordType", constant("Company Number"))
                .setHeader("Destination", constant("{{endpoint.output}}"))
                .setHeader("Upload", constant("{{endpoint.s3.upload}}"))
                .setHeader("Presign", constant("{{endpoint.s3presigner.download}}"))
                .setHeader("AggregationModelId", constant("company-number-mongo-alpha"))
                .setHeader(AWS2S3Constants.KEY, simple("company/collection_alpha_mongo_${date:now:yyyyMMdd}T${date:now:hhmmss}.csv"))
                .setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, constant("{{aws.expiry}}"))
                .to("{{function.name.compare_collection}}");
    }
}
