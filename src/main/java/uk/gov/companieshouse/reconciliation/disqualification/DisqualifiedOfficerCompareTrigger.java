package uk.gov.companieshouse.reconciliation.disqualification;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.springframework.stereotype.Component;

/**
 * Trigger a comparison between disqualified officers on Oracle with disqualifications on MongoDB.
 */
@Component
public class DisqualifiedOfficerCompareTrigger extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.dsq_officer_collection.cron.tab}}")
                .setHeader("OracleQuery", simple("{{query.oracle.dsq_officer_collection}}"))
                .setHeader("OracleEndpoint", simple("{{endpoint.oracle.dsq_officer_collection}}"))
                .setHeader("OracleDescription", constant("Oracle"))
                .setHeader("OracleTargetHeader", constant("SrcList"))
                .setHeader("Src", simple("{{endpoint.oracle.collection}}"))
                .setHeader("MongoEndpoint", simple("{{endpoint.mongodb.disqualifications_collection}}"))
                .setHeader("MongoDescription", constant("MongoDB"))
                .setHeader("MongoTargetHeader", constant("TargetList"))
                .setHeader(MongoDbConstants.DISTINCT_QUERY_FIELD, constant("officer_id_raw"))
                .setHeader("Target", simple("{{endpoint.mongodb.wrapper.disqualifications.collection}}"))
                .setHeader("Comparison", simple("disqualified officers"))
                .setHeader("RecordType", constant("Disqualified Officer"))
                .setHeader("Destination", simple("{{endpoint.dsq_officer.output}}"))
                .setHeader("Upload", simple("{{endpoint.s3.upload}}"))
                .setHeader("Presign", simple("{{endpoint.s3presigner.download}}"))
                .setHeader(AWS2S3Constants.KEY, simple("dsq_officer/collection_${date:now:yyyyMMdd}-${date:now:hhmmss}.csv"))
                .setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, simple("{{aws.expiry}}"))
                .to("{{function.name.compare_collection}}");
    }
}
