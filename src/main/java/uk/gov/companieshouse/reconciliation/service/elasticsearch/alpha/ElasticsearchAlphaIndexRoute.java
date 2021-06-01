package uk.gov.companieshouse.reconciliation.service.elasticsearch.alpha;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchAlphaIndexRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:elasticsearch-alpha")
                .bean(ElasticsearchAlphaIndexTransformer.class);
    }
}
