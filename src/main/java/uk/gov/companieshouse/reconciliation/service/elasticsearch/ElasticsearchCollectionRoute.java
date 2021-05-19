package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.caffeine.CaffeineConstants;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client.ElasticsearchSlicedScrollIterator;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Retrieves hits from an Elasticsearch search index.<br>
 * <br>
 * IN:<br>
 * <br>
 * header(ElasticsearchQuery): The query that will be run against Elasticsearch.<br>
 * header(ElasticsearchLogIndices): An {@link java.lang.Integer integer} used to determine the interval at which the
 * number of search hits will be logged.<br>
 * header(ElasticsearchDescription): A description of the {@link ResourceList resource list} that will be aggregated.<br>
 * header(ElasticsearchTargetHeader): The header in which results will be aggregated as a {@link ResourceList resource list}.<br>
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
                .when(body().isNull())
                    .setBody(header("ElasticsearchQuery"))
                    .toD("${header.ElasticsearchEndpoint}")
                    .bean(ElasticsearchTransformer.class)
                    .setHeader(CaffeineConstants.ACTION, constant(CaffeineConstants.ACTION_PUT))
                    .setHeader(CaffeineConstants.KEY, simple("${header.ElasticsearchCacheKey}"))
                    .to("{{endpoint.cache}}")
                .end();
    }
}
