package uk.gov.companieshouse.reconciliation.insolvency;

import com.mongodb.client.model.Filters;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws2.s3.AWS2S3Constants;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.springframework.stereotype.Component;

/**
 * Trigger a comparison between companies with insolvency cases on Oracle and MongoDB.
 */
@Component
public class InsolvencyCompanyNumberCompare extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("{{endpoint.insolvency.company_number.timer}}")
                .setHeader("OracleQuery", simple("{{query.oracle.insolvency_company_number}}"))
                .setHeader("OracleEndpoint", simple("{{endpoint.oracle.list}}"))
                .setHeader("SrcDescription", constant("Oracle"))
                .setHeader("Src", simple("{{endpoint.oracle.collection}}"))
                .setHeader("TargetDescription", constant("MongoDB"))
                .setHeader("Target", constant("{{endpoint.mongodb.wrapper.distinct.collection}}"))
                .setHeader(MongoDbConstants.DISTINCT_QUERY_FIELD, constant("_id"))
                .setHeader(MongoDbConstants.CRITERIA, constant(Filters.exists("data.cases.number", true)))
                .setHeader("MongoDistinctEndpoint", constant("{{endpoint.mongodb.insolvency_collection}}"))
                .setHeader("MongoDistinctCacheKey", constant("{{endpoint.mongodb.insolvency.cache.key}}"))
                .setHeader("Comparison", constant("company insolvency cases"))
                .setHeader("ComparisonGroup", constant("Company insolvency"))
                .setHeader("RecordType", constant("Company Number"))
                .setHeader("Destination", simple("{{endpoint.output}}"))
                .setHeader("Upload", simple("{{endpoint.s3.upload}}"))
                .setHeader("Presign", simple("{{endpoint.s3presigner.download}}"))
                .setHeader(AWS2S3Constants.KEY, simple("insolvency/insolvency_company_number_${date:now:yyyyMMdd}T${date:now:hhmmss}.csv"))
                .setHeader(AWS2S3Constants.DOWNLOAD_LINK_EXPIRATION_TIME, constant("{{aws.expiry}}"))
                .to("{{function.name.compare_collection}}");
    }
}
