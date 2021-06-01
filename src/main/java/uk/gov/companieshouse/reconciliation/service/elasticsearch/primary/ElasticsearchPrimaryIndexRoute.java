package uk.gov.companieshouse.reconciliation.service.elasticsearch.primary;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchPrimaryIndexRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:elasticsearch-primary")
                .bean(ElasticsearchPrimaryIndexTransformer.class);
    }
}

