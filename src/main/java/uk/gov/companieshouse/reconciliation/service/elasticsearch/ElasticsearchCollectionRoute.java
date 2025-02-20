package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.LoggingLevel;
import org.apache.camel.component.caffeine.CaffeineConstants;
import org.elasticsearch.ElasticsearchStatusException;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.common.RetryableRoute;
import uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client.ElasticsearchException;

/**
 * Retrieves hits from an Elasticsearch search index.<br>
 * <br>
 * IN:<br>
 * <br>
 * header(ElasticsearchQuery): The query that will be run against Elasticsearch.<br>
 * header(ElasticsearchLogIndices): An {@link java.lang.Integer integer} used to determine the interval at which the
 * number of search hits will be logged.<br>
 * header(ElasticsearchCacheKey): The key under which results will be cached.<br>
 * header(ElasticsearchEndpoint): The Elasticsearch service that indices will be fetched from.<br>
 * header(ElasticsearchTransformer): The name of the route responsible for transforming indices returned by
 * Elasticsearch.
 */
@Component
public class ElasticsearchCollectionRoute extends RetryableRoute {

    @Override
    public void configure() {
        super.configure();
        from("direct:elasticsearch-collection")
                .onException(ElasticsearchException.class)
                    .handled(true)
                    .setHeader("Failed").constant(true)
                    .log(LoggingLevel.ERROR, "Failed to rertieve results from Elasticsearch")
                .end()
                .onException(ElasticsearchStatusException.class)
                    .handled(true)
                    .setHeader("Failed").constant("true")
                    .log(LoggingLevel.ERROR, "Elasticsearch returned a status exception")
                .end()
                .setHeader(CaffeineConstants.ACTION, constant(CaffeineConstants.ACTION_GET))
                .setHeader(CaffeineConstants.KEY, simple("${header.ElasticsearchCacheKey}"))
                .to("{{endpoint.cache}}")
                .choice()
                .when(header(CaffeineConstants.ACTION_HAS_RESULT).isEqualTo(false))
                    .setBody(header("ElasticsearchQuery"))
                    .toD("${header.ElasticsearchEndpoint}")
                    .toD("${header.ElasticsearchTransformer}")
                    .log("${body.size()} results have been fetched from elasticsearch.")
                    .setHeader(CaffeineConstants.ACTION, constant(CaffeineConstants.ACTION_PUT))
                    .setHeader(CaffeineConstants.KEY, simple("${header.ElasticsearchCacheKey}"))
                    .to("{{endpoint.cache}}")
                .otherwise()
                    .log("${body.size()} results have been fetched from the cache.")
                .end();
    }
}
