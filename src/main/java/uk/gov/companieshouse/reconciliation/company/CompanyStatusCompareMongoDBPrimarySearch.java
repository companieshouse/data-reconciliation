package uk.gov.companieshouse.reconciliation.company;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class CompanyStatusCompareMongoDBPrimarySearch extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.company_status_mongo_primary.timer}}")
                .autoStartup("{{company_status_mongo_primary_enabled}}")
                .setHeader("Src").constant("{{endpoint.mongodb.wrapper.aggregation.collection}}")
                .setHeader("SrcDescription").constant("MongoDB - Company Profile")
                .setHeader("MongoCacheKey").constant("{{endpoint.mongodb.company_profile.cache.key}}")
                .setHeader("MongoQuery").constant(Collections.singletonList(Aggregates.project(Projections.include("_id", "data.company_name", "data.company_status"))))
                .setHeader("MongoEndpoint").constant("{{endpoint.mongodb.company_profile_collection}}")
                .setHeader("MongoTransformer").constant("{{transformer.mongo.company_profile}}")
                .setHeader("Target").constant("{{endpoint.elasticsearch.collection}}")
                .setHeader("TargetDescription").constant("Primary Search Index")
                .setHeader("ElasticsearchEndpoint").constant("{{endpoint.elasticsearch.primary}}")
                .setHeader("ElasticsearchQuery").constant("{{query.elasticsearch.primary.company}}")
                .setHeader("ElasticsearchCacheKey").constant("{{endpoint.elasticsearch.primary.cache.key}}")
                .setHeader("ElasticsearchTransformer").constant("{{transformer.elasticsearch.primary}}")
                .setHeader("RecordKey").constant("Company Number")
                .setHeader("ComparisonGroup", constant("Elasticsearch"))
                .setHeader("ComparisonDescription", constant("company status comparison between MongoDB and primary index"))
                .setHeader("Destination").constant("{{endpoint.output}}")
                .setHeader("ResultsTransformer").constant("{{function.mapper.company_status}}")
                .setHeader("Upload", constant("{{endpoint.s3.upload}}"))
                .setHeader("Presign", constant("{{endpoint.s3presigner.download}}"))
                .setHeader("AggregationModelId", constant("company-status-mongo-primary"))
                .setHeader(AWS2S3Constants.KEY, simple("company/results_status_primary_mongo_${date:now:yyyyMMdd}T${date:now:hhmmss}.csv"))
                .setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, constant("{{aws.expiry}}"))
                .to("{{function.name.compare_results}}");
    }

}
