package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.service.transformer.ResultsToCompanyNumberTransformer;

@Component
public class ElasticsearchCompanyNumberMapper extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:elasticsearch-company_number-mapper")
                .to("{{endpoint.elasticsearch.collection}}")
                .bean(ResultsToCompanyNumberTransformer.class);
    }
}
