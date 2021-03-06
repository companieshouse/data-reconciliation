package uk.gov.companieshouse.reconciliation.company;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Triggers a comparison between company status in MongoDB and Oracle; any differences will be
 * recorded in the results.
 */
@Component
public class CompanyStatusCompareMongoDBOracle extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.company_status_mongo_oracle.timer}}")
                .autoStartup("{{company_status_mongo_oracle_enabled}}")
                .setHeader("Target").constant("{{endpoint.mongodb.wrapper.aggregation.collection}}")
                .setHeader("TargetDescription").constant("MongoDB - Company Profile")
                .setHeader("MongoCacheKey").constant("{{endpoint.mongodb.company_profile.cache.key}}")
                .setHeader("MongoQuery").constant(Collections.singletonList(Aggregates.project(Projections.include("_id", "data.company_name", "data.company_status"))))
                .setHeader("MongoEndpoint").constant("{{endpoint.mongodb.company_profile_collection}}")
                .setHeader("MongoTransformer").constant("{{transformer.mongo.company_profile}}")
                .setHeader("Src").constant("{{endpoint.oracle.multi}}")
                .setHeader("SrcDescription").constant("Oracle")
                .setHeader("OracleQuery").constant("{{queries.oracle.company_status}}")
                .setHeader("OracleEndpoint").constant("{{endpoint.oracle.list}}")
                .setHeader("RecordKey").constant("Company Number")
                .setHeader("ComparisonGroup").constant("Company profile")
                .setHeader("ComparisonDescription", constant("company status comparison between MongoDB and Oracle"))
                .setHeader("Destination").constant("{{endpoint.output}}")
                .setHeader("ResultsTransformer").constant("{{function.mapper.company_status}}")
                .setHeader("Upload", constant("{{endpoint.s3.upload}}"))
                .setHeader("Presign", constant("{{endpoint.s3presigner.download}}"))
                .setHeader("AggregationModelId", constant("company-status-mongo-oracle"))
                .setHeader(AWS2S3Constants.KEY, simple("company/results_status_oracle_mongo_${date:now:yyyyMMdd}T${date:now:hhmmss}.csv"))
                .setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, constant("{{aws.expiry}}"))
                .to("{{function.name.compare_results}}");
    }
}
