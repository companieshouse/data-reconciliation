package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchCompanyNumberDispatcher extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:elasticsearch-dispatcher")
                .toD("${header.ElasticsearchEndpoint}")
                .bean(ElasticsearchCompanyNumberTranformer.class);
    }
}
