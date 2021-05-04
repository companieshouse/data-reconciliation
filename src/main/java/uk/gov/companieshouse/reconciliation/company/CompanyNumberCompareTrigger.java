package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.springframework.stereotype.Component;

/**
 * Trigger a comparison between corporate bodies' and foreign branches' incorporation numbers on Oracle with company
 * profile IDs on MongoDB.
 */
@Component
public class CompanyNumberCompareTrigger extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.company_collection.cron.tab}}")
                .setBody(constant("{{query.oracle.corporate_body_collection}}"))
                .setHeader("Src", simple("{{endpoint.oracle.corporate_body_collection}}"))
                .setHeader("SrcName", simple("Oracle"))
                .setHeader("Target", simple("{{endpoint.mongodb.company_profile_collection}}"))
                .setHeader("TargetName", simple("MongoDB"))
                .setHeader("Comparison", simple("company profiles"))
                .setHeader("CompanyCollection", constant("CompanyCollection"))
                .setHeader("Upload", simple("{{endpoint.s3.upload}}"))
                .setHeader("Presign", simple("{{endpoint.s3presigner.download}}"))
                .setHeader(AWS2S3Constants.KEY, simple("company/collection_${date:now:yyyyMMdd}.csv"))
                .setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, simple("{{aws.expiry}}"))
                .setHeader(MongoDbConstants.DISTINCT_QUERY_FIELD, constant("_id"))
                .to("{{function.name.compare_collection}}");
    }
}
