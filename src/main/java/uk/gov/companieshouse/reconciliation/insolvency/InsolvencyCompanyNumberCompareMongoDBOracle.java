package uk.gov.companieshouse.reconciliation.insolvency;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;

/**
 * Trigger a comparison between companies with insolvency cases on MongoDB and Oracle.
 */
@Component
public class InsolvencyCompanyNumberCompareMongoDBOracle extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.insolvency.company_number_mongo_oracle.timer}}")
                .autoStartup("{{insolvency_company_number_mongo_oracle_enabled}}")
                .setHeader("SrcDescription", constant("MongoDB"))
                .setHeader("Src", constant("{{endpoint.mongodb.mapper.collection.insolvency}}"))
                .setHeader("TargetDescription", constant("Oracle"))
                .setHeader("Target", constant("{{endpoint.oracle.collection}}"))
                .setHeader("OracleQuery", constant("{{query.oracle.insolvency_company_number}}"))
                .setHeader("OracleEndpoint", constant("{{endpoint.oracle.list}}"))
                .setHeader("OracleTransformer", constant("{{transformer.oracle.single_column}}"))
                .setHeader("Comparison", constant("company insolvency cases"))
                .setHeader("ComparisonGroup", constant("Company insolvency"))
                .setHeader("RecordType", constant("Company Number"))
                .setHeader("Destination", constant("{{endpoint.output}}"))
                .setHeader("Upload", constant("{{endpoint.s3.upload}}"))
                .setHeader("Presign", constant("{{endpoint.s3presigner.download}}"))
                .setHeader("AggregationModelId", constant("insolvency-company-number-mongo-oracle"))
                .setHeader(AWS2S3Constants.KEY, simple("insolvency/insolvency_company_number_${date:now:yyyyMMdd}T${date:now:hhmmss}.csv"))
                .setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, constant("{{aws.expiry}}"))
                .to("{{function.name.compare_collection}}");
    }
}
