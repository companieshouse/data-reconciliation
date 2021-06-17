package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Retrieve search hits from Elasticsearch and transform results into a {@link uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList resource list}
 * of company numbers.<br>
 * <br>
 * IN:<br>
 * <br>
 * header(Description): A description of the results produced by this pipeline.<br>
 * <br>
 * OUT:<br>
 * <br>
 * body(): Company numbers of company profiles fetched from Elasticsearch.<br>
 */

@Component
public class ElasticsearchCompanyNumberMapper extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:elasticsearch-company_number-mapper")
                .to("{{endpoint.elasticsearch.collection}}")
                .choice()
                .when(header("Failed").isNotEqualTo(true))
                    .bean(ElasticsearchCompanyNumberTransformer.class)
                .end();
    }
}
