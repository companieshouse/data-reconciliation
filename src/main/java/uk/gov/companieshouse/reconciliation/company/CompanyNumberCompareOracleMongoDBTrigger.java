package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.springframework.stereotype.Component;

/**
 * Trigger a comparison between corporate bodies' and foreign branches' incorporation numbers on Oracle with company
 * profile IDs on MongoDB.
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
                .setHeader("Target", simple("{{endpoint.mongodb.collection}}"))
                .setHeader("Destination", simple("{{endpoint.output}}"))
                .setHeader(MongoDbConstants.DISTINCT_QUERY_FIELD, constant("_id"))
                .to("{{function.name.compare_collection}}");
    }
}
