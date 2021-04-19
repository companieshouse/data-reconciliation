package uk.gov.companieshouse.reconciliation.disqualification;

import org.apache.camel.builder.RouteBuilder;
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
                .setBody(constant("{{query.oracle.dsq_officer_collection}}"))
                .setHeader("Src", simple("{{endpoint.oracle.dsq_officer_collection}}"))
                .setHeader("SrcName", simple("Oracle"))
                .setHeader("Target", simple("{{endpoint.mongodb.disqualifications_collection}}"))
                .setHeader("TargetName", simple("MongoDB"))
                .setHeader("Destination", simple("{{endpoint.output}}"))
                .setHeader(MongoDbConstants.DISTINCT_QUERY_FIELD, constant("officer_id_raw"))
                .to("{{function.name.compare_collection}}");
    }
}
