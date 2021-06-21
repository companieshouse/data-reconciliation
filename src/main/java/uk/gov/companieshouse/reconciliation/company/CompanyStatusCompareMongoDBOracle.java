package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.springframework.stereotype.Component;

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
                .setHeader("Src").constant("{{endpoint.mongodb.wrapper.aggregation.collection}}")
                .setHeader("SrcDescription").constant("MongoDB - Company Profile")
                .setHeader("Target").constant("{{endpoint.oracle.multi}}")
                .setHeader("TargetDescription").constant("Oracle")
                .setHeader("OracleQuery").constant("{{queries.oracle.company_status}}")
                .setHeader("OracleEndpoint").constant("{{endpoint.oracle.list}}")
                .setHeader("RecordKey").constant("Company Number")
                .setHeader("Comparison").constant("company statuses")
                .setHeader("ComparisonGroup").constant("Company profile")
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
