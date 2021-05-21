package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MongoCompanyNumberMapper extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:mongo-company_number-mapper")
                .to("{{endpoint.mongodb.wrapper.company_profile.collection}}")
                .bean(MongoCompanyNumberTransformer.class);
    }
}
