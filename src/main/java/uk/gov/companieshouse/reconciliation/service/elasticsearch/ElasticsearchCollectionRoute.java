package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.caffeine.CaffeineConstants;
import org.springframework.stereotype.Component;

/**
 * Retrieves hits from an Elasticsearch search index.<br>
 * <br>
 * IN:<br>
 * <br>
 * header(ElasticsearchQuery): The query that will be run against Elasticsearch.<br>
 * header(ElasticsearchLogIndices): An {@link java.lang.Integer integer} used to determine the interval at which the
 * number of search hits will be logged.<br>
 */
@Component
public class ElasticsearchCollectionRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:elasticsearch-collection")
                .setHeader(CaffeineConstants.ACTION, constant(CaffeineConstants.ACTION_GET))
                .setHeader(CaffeineConstants.KEY, simple("${header.ElasticsearchCacheKey}"))
                .to("{{endpoint.cache}}")
                .choice()
                .when(header(CaffeineConstants.ACTION_HAS_RESULT).isEqualTo(false))
                    .setBody(header("ElasticsearchQuery"))
                    .toD("${header.ElasticsearchEndpoint}")
                    .bean(ElasticsearchTransformer.class)
                    .log("${body.size()} results have been fetched from elasticsearch.")
                    .setHeader(CaffeineConstants.ACTION, constant(CaffeineConstants.ACTION_PUT))
                    .setHeader(CaffeineConstants.KEY, simple("${header.ElasticsearchCacheKey}"))
                    .to("{{endpoint.cache}}")
                .otherwise()
                    .log("${body.size()} results have been fetched from the cache.")
                .end();
    }
}
