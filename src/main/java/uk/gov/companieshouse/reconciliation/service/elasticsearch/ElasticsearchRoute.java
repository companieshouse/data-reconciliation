package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.Collections;
import java.util.LinkedList;

@Component
public class ElasticsearchRoute extends RouteBuilder {

    @Value("${endpoint.elasticsearch.threads}")
    private int numberOfThreads;

    @Override
    public void configure() throws Exception {
        from("direct:elasticsearch")
                .setBody(header("ElasticsearchQuery"))
                .to("${header.ElasticsearchEndpoint}")
                .split()
                .body()
                .streaming()
                .parallelProcessing()
                .threads(numberOfThreads, numberOfThreads)
                .aggregate((AggregationStrategy) (prev, curr) -> {
                    if (prev == null) {
                        ResourceList elasticsearchResults = new ResourceList(Collections.synchronizedList(new LinkedList<>()), "Elasticsearch");
                        elasticsearchResults.add(curr.getIn().getBody(SearchHit.class).getId());
                        curr.getIn().setHeader("ElasticList", elasticsearchResults);
                        return curr;
                    }
                    ResourceList results = prev.getIn().getHeader("ElasticList", ResourceList.class);
                    results.add(curr.getIn().getBody(SearchHit.class).getId());
                    if(results.size() % 100000 == 0) {
                        this.log.info("Indexed {} entries", results.size());
                    }
                    return prev;
                })
                .constant(true)
                .completionTimeout(30000L)
                .stop();
    }
}
