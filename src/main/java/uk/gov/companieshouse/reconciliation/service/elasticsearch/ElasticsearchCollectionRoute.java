package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.builder.RouteBuilder;
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

    @Value("${results.initial.capacity}")
    private int initialCapacity;

    @Override
    public void configure() throws Exception {
        from("direct:elasticsearch-collection")
                .setBody(header("ElasticsearchQuery"))
                .enrich()
                .simple("${header.ElasticsearchEndpoint}")
                .aggregationStrategy((curr, es) -> {
                    ResourceList indices = new ResourceList(new HashSet<>(initialCapacity), curr.getIn().getHeader("ElasticsearchDescription", String.class));
                    Iterator<SearchHit> it = es.getIn().getBody(ElasticsearchSlicedScrollIterator.class);
                    while (it.hasNext()) {
                        indices.add(it.next().getId());
                        Integer logIndices = curr.getIn().getHeader("ElasticsearchLogIndices", Integer.class);
                        if (logIndices != null && indices.size() % logIndices == 0) {
                            this.log.info("Indexed {} entries", indices.size());
                        }
                    }
                    this.log.info("Indexed {} entries", indices.size());
                    curr.getIn().setHeader(curr.getIn().getHeader("ElasticsearchTargetHeader", String.class), indices);
                    return curr;
                });
    }
}
