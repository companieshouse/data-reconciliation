package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;

/**
 * Trigger a comparison of corporate body counts in Oracle and MongoDB.
 */
@Component
public class CompanyCountTrigger extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.company_count.cron.tab}}")
                .setBody(constant("{{query.oracle.corporate_body_count}}"))
                .setHeader("Src", simple("{{endpoint.oracle.corporate_body_count}}"))
                .setHeader("SrcName", simple("Oracle"))
                .setHeader("Target", simple("{{endpoint.mongodb.company_profile_count}}"))
                .setHeader("TargetName", simple("MongoDB"))
                .setHeader("Comparison", simple("company profiles"))
                .setHeader("Destination", simple("{{endpoint.output}}"))
                .setHeader("Upload", simple("{{endpoint.s3.company_profile_count}}"))
                .setHeader("Presign", simple("{{endpoint.s3presigner.company_profile_count}}"))
                .setHeader(AWS2S3Constants.KEY, constant("company/count_${date:now:yyyyMMdd}.csv"))
                .to("{{function.name.compare_count}}");
    }
}
