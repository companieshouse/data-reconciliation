package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;

/**
 * Trigger a comparison between corporate bodies' and foreign branches' incorporation numbers on Oracle with company
 * profile IDs on MongoDB.
 *
 * The following request headers should be set when a message is sent to this route:
 *
 * AWS2S3Constants.KEY: The key (name) which should be appended to CSV files.
 * AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME: The time which can be configured to expire download links.
 */
@Component
public class CompanyNumberCompareOracleMongoDBTrigger extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.company_number_mongo_oracle.timer}}")
                .autoStartup("{{company_number_mongo_oracle_enabled}}")
                .setHeader("OracleQuery", constant("{{query.oracle.corporate_body_collection}}"))
                .setHeader("OracleEndpoint", constant("{{endpoint.oracle.list}}"))
                .setHeader("OracleTransformer", constant("{{transformer.oracle.single_column}}"))
                .setHeader("SrcDescription", constant("Oracle"))
                .setHeader("Src", constant("{{endpoint.oracle.collection}}"))
                .setHeader("MongoEndpoint", constant("{{endpoint.mongodb.company_profile_collection}}"))
                .setHeader("TargetDescription", constant("MongoDB"))
                .setHeader("Target", constant("{{endpoint.mongodb.mapper.collection.company_number}}"))
                .setHeader("ComparisonGroup", constant("Company profile"))
                .setHeader("ComparisonDescription", constant("company number comparison between MongoDB and Oracle"))
                .setHeader("RecordType", constant("Company Number"))
                .setHeader("Destination", constant("{{endpoint.output}}"))
                .setHeader("Upload", constant("{{endpoint.s3.upload}}"))
                .setHeader("Presign", constant("{{endpoint.s3presigner.download}}"))
                .setHeader("AggregationModelId", constant("company-number-mongo-oracle"))
                .setHeader(AWS2S3Constants.KEY, simple("company/collection_${date:now:yyyyMMdd}T${date:now:hhmmss}.csv"))
                .setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, constant("{{aws.expiry}}"))
                .to("{{function.name.compare_collection}}");
    }
}
