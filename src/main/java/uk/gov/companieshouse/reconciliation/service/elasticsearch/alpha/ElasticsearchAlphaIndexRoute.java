package uk.gov.companieshouse.reconciliation.service.elasticsearch.alpha;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

/**
 * Transform indices returned by Elasticsearch alpha index.
 * <br>
 * IN:<br>
 * body(): An {@link java.util.Iterator iterator} from which {@link org.elasticsearch.search.SearchHit search hits}
 * can be obtained.
 */
@Component
public class ElasticsearchAlphaIndexRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        errorHandler(noErrorHandler());
        from("direct:elasticsearch-alpha")
                .bean(ElasticsearchAlphaIndexTransformer.class);
    }
}
