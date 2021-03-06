package uk.gov.companieshouse.reconciliation.disqualification;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Trigger a comparison between disqualified officers on Oracle with disqualifications on MongoDB.
 */
@Component
public class DisqualifiedOfficerIdMongoDBOracleCompareTrigger extends RouteBuilder {

    @Autowired
    private List<Bson> disqualifiedOfficerAggregationQuery;

    @Override
    public void configure() throws Exception {
        from("{{endpoint.dsq_officer_id_mongo_oracle.timer}}")
                .autoStartup("{{dsq_officer_id_mongo_oracle_enabled}}")
                .setHeader("OracleQuery", constant("{{query.oracle.dsq_officer_collection}}"))
                .setHeader("OracleEndpoint", constant("{{endpoint.oracle.dsq_officer_collection}}"))
                .setHeader("OracleTransformer", constant("{{transformer.oracle.single_column}}"))
                .setHeader("SrcDescription", constant("Oracle"))
                .setHeader("Src", constant("{{endpoint.oracle.collection}}"))
                .setHeader("TargetDescription", constant("MongoDB"))
                .setHeader("Target", constant("{{endpoint.mongodb.mapper.collection.dsq_officer}}"))
                .setHeader("ComparisonGroup", constant("Disqualified officer"))
                .setHeader("ComparisonDescription", constant("disqualified officer ID comparison between MongoDB and Oracle"))
                .setHeader("RecordType", constant("Disqualified Officer"))
                .setHeader("Destination", constant("{{endpoint.output}}"))
                .setHeader("Upload", constant("{{endpoint.s3.upload}}"))
                .setHeader("Presign", constant("{{endpoint.s3presigner.download}}"))
                .setHeader("AggregationModelId", constant("dsq-officer-id-mongo-oracle"))
                .setHeader(AWS2S3Constants.KEY, simple("dsq_officer/collection_${date:now:yyyyMMdd}-${date:now:hhmmss}.csv"))
                .setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, constant("{{aws.expiry}}"))
                .to("{{function.name.compare_collection}}");
    }
}
