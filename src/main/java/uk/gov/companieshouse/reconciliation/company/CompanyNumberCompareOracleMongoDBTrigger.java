package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.springframework.stereotype.Component;

/**
 * Trigger a comparison between corporate bodies' and foreign branches' incorporation numbers on Oracle with company
 * profile IDs on MongoDB.
 *
 * The following request headers should be set when a message is sent to this route:
 *
 * AWS2S3Constants.KEY: The key (name) which should be appended to CSV files.
 * AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME: The time which can be configured to expire download links.
 * MongoDbConstants.DISTINCT_QUERY_FIELD: The unique field used as an identifier for MongoDB
 */
@Component
public class CompanyNumberCompareOracleMongoDBTrigger extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.company_collection.cron.tab}}")
                .setHeader("OracleQuery", simple("{{query.oracle.corporate_body_collection}}"))
                .setHeader("OracleEndpoint", simple("{{endpoint.oracle.corporate_body_collection}}"))
                .setHeader("OracleDescription", constant("Oracle"))
                .setHeader("OracleTargetHeader", constant("SrcList"))
                .setHeader("Src", simple("{{endpoint.oracle.collection}}"))
                .setHeader("MongoEndpoint", simple("{{endpoint.mongodb.company_profile_collection}}"))
                .setHeader("MongoDescription", constant("MongoDB"))
                .setHeader("MongoTargetHeader", constant("TargetList"))
                .setHeader(MongoDbConstants.DISTINCT_QUERY_FIELD, constant("_id"))
                .setHeader("Target", simple("{{endpoint.mongodb.mapper.collection.company_number}}"))
                .setHeader("Comparison", constant("company numbers"))
                .setHeader("ComparisonGroup", constant("Company profile"))
                .setHeader("RecordType", constant("Company Number"))
                .setHeader("Destination", simple("{{endpoint.output}}"))
                .setHeader("Upload", simple("{{endpoint.s3.upload}}"))
                .setHeader("Presign", simple("{{endpoint.s3presigner.download}}"))
                .setHeader(AWS2S3Constants.KEY, simple("company/collection_${date:now:yyyyMMdd}T${date:now:hhmmss}.csv"))
                .setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, constant("{{aws.expiry}}"))
                .to("{{function.name.compare_collection}}");
    }
}
