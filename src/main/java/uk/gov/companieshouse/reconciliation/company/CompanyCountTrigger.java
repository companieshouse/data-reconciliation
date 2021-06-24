package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;

/**
 * Trigger a comparison of corporate body counts in Oracle and MongoDB.
 *
 * The following request headers should be set when a message is sent to this route:
 *
 * AWS2S3Constants.KEY: The key (name) which should be appended to CSV files.
 * AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME: The time which can be configured to expire download links.
 *
 */
@Component
public class CompanyCountTrigger extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.company_count.timer}}")
                .setBody(constant("{{query.oracle.corporate_body_count}}"))
                .setHeader("Src", constant("{{endpoint.oracle.single}}"))
                .setHeader("SrcName", constant("Oracle"))
                .setHeader("Target", constant("{{endpoint.mongodb.company_profile_count}}"))
                .setHeader("TargetName", constant("MongoDB"))
                .setHeader("Comparison", constant("company profiles"))
                .setHeader("ComparisonGroup", constant("Company profile"))
                .setHeader("Destination", constant("{{endpoint.output}}"))
                .setHeader("Upload", constant("{{endpoint.s3.upload}}"))
                .setHeader("Presign", constant("{{endpoint.s3presigner.download}}"))
                .setHeader("LinkId", constant("company-count-link"))
                .setHeader(AWS2S3Constants.KEY, simple("company/count_${date:now:yyyyMMdd}T${date:now:hhmmss}.csv"))
                .setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, constant("{{aws.expiry}}"))
                .to("{{function.name.compare_count}}");
    }
}
