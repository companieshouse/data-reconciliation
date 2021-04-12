package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
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
                .setHeader("Destination", simple("{{endpoint.output}}"))
                .setHeader(MongoDbConstants.DISTINCT_QUERY_FIELD, constant("_id"))
                .to("{{function.name.compare_collection}}");
    }
}
