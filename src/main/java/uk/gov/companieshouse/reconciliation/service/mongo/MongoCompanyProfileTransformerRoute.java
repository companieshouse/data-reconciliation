package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MongoCompanyProfileTransformerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:company-profile-transformer")
                .bean(MongoCompanyProfileTransformer.class);
    }
}
