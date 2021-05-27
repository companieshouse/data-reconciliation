package uk.gov.companieshouse.reconciliation.company;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

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
                .to("{{function.name.compare_results}}");
    }
}
