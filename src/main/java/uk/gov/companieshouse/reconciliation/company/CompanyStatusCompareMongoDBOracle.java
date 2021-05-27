package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Triggers a comparison between company status in MongoDB and Oracle; any differences will be
 * recorded in the results.
 */
@Component
public class CompanyStatusCompareMongoDBOracle extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{endpoint.company_status_mongo_oracle.cron.tab}}")
                .setHeader("Src").constant("{{endpoint.mongodb.wrapper.company_profile.collection}}")
                .setHeader("SrcDescription").constant("MongoDB - Company Profile")
                .setHeader("Target").constant("{{endpoint.oracle.multi}}")
                .setHeader("TargetDescription").constant("Oracle")
                .setHeader("OracleQuery").constant("{{queries.oracle.company_status}}")
                .setHeader("OracleEndpoint").constant("{{endpoint.oracle.corporate_body_collection}}")
                .setHeader("RecordType").constant("Company Status")
                .setHeader("Destination").constant("{{endpoint.log.output}}")
                .setHeader("Transformer").constant("{{function.mapper.company_statuses}}")
                .to("{{function.name.compare_results}}");
    }
}
