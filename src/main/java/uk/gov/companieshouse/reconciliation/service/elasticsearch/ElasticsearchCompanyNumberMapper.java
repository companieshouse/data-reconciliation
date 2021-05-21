package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ElasticsearchCompanyNumberMapper extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:elasticsearch-company_number-mapper")
                .to("{{endpoint.elasticsearch.collection}}")
                .bean(ElasticsearchCompanyNumberTransformer.class);
    }
}
