package uk.gov.companieshouse.reconciliation.service.elasticsearch.primary;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Transform indices returned by Elasticsearch primary index.<br>
 * <br>
 * IN:<br>
 * body(): An {@link java.util.Iterator iterator} from which {@link org.elasticsearch.search.SearchHit search hits}
 * can be obtained.
 */
@Component
public class ElasticsearchPrimaryIndexRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:elasticsearch-primary")
                .bean(ElasticsearchPrimaryIndexTransformer.class);
    }
}

