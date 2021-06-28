package uk.gov.companieshouse.reconciliation.insolvency;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Trigger a comparison between counts of company insolvency cases on MongoDB and Oracle.
 */
@Component
public class InsolvencyCaseCountCompareMongoDBOracle extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.insolvency.case_count_mongo_oracle.timer}}")
                .autoStartup("{{insolvency_case_count_mongo_oracle_enabled}}")
                .setHeader("Src").constant("{{endpoint.mongodb.wrapper.aggregation.collection}}")
                .setHeader("SrcDescription").constant("MongoDB - Number of cases")
                .setHeader("MongoCacheKey").constant("{{endpoint.mongodb.insolvency_cases.cache.key}}")
                .setHeader("MongoQuery").constant(Arrays.asList(
                        Aggregates.match(Filters.exists("data.cases.number")),
                        Aggregates.project(Projections.fields(
                                Projections.include("_id"),
                                Projections.computed("cases", new Document("$size", "$data.cases.number"))))))
                .setHeader("MongoEndpoint").constant("{{endpoint.mongodb.insolvency_aggregation_collection}}")
                .setHeader("MongoTransformer").constant("{{transformer.mongo.insolvency_cases}}")
                .setHeader("Target").constant("{{endpoint.oracle.collection}}")
                .setHeader("TargetDescription").constant("Oracle DB - Number of cases")
                .setHeader("OracleQuery").constant("{{query.oracle.insolvency_cases}}")
                .setHeader("OracleEndpoint").constant("{{endpoint.oracle.list}}")
                .setHeader("OracleTransformer").constant("{{transformer.oracle.insolvency_cases}}")
                .setHeader("RecordKey").constant("Company Number")
                .setHeader("Comparison").constant("company insolvency case counts")
                .setHeader("ComparisonGroup").constant("Company insolvency")
                .setHeader("Destination").constant("{{endpoint.output}}")
                .setHeader("ResultsTransformer").constant("{{function.mapper.insolvency_cases}}")
                .setHeader("Upload", constant("{{endpoint.s3.upload}}"))
                .setHeader("Presign", constant("{{endpoint.s3presigner.download}}"))
                .setHeader("AggregationModelId", constant("insolvency-case-count-mongo-oracle"))
                .setHeader(AWS2S3Constants.KEY, simple("company/insolvency_cases_${date:now:yyyyMMdd}T${date:now:hhmmss}.csv"))
                .setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, constant("{{aws.expiry}}"))
                .to("{{function.name.compare_results}}");
    }
}
