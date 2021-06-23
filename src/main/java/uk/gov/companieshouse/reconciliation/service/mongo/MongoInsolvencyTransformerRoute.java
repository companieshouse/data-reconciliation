package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class MongoInsolvencyTransformerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:mongo-insolvency_cases-transformer")
                .bean(MongoInsolvencyTransformer.class);
    }
}
