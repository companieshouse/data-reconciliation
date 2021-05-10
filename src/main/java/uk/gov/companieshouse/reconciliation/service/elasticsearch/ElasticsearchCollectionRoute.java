package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.builder.RouteBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.Collections;
import java.util.LinkedList;

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

    @Value("${endpoint.elasticsearch.threads}")
    private int numberOfThreads;

    @Override
    public void configure() throws Exception {
        from("direct:elasticsearch-collection")
                .setBody(header("ElasticsearchQuery"))
                .toD("${header.ElasticsearchEndpoint}")
                .split()
                .body()
                .aggregationStrategy((prev, curr) -> {
                    if (prev == null) {
                        ResourceList elasticsearchResults = new ResourceList(Collections.synchronizedList(new LinkedList<>()), curr.getIn().getHeader("ElasticsearchDescription", String.class));
                        elasticsearchResults.add(curr.getIn().getBody(SearchHit.class).getId());
                        curr.getIn().setHeader(curr.getIn().getHeader("ElasticsearchTargetHeader", String.class), elasticsearchResults);
                        return curr;
                    }
                    ResourceList results = prev.getIn().getHeader(prev.getIn().getHeader("ElasticsearchTargetHeader", String.class), ResourceList.class);
                    results.add(curr.getIn().getBody(SearchHit.class).getId());
                    Integer logIndices = curr.getIn().getHeader("ElasticsearchLogIndices", Integer.class);
                    if(logIndices != null && results.size() % logIndices == 0) {
                        this.log.info("Indexed {} entries", results.size());
                    }
                    return prev;
                })
                .process();
    }
}