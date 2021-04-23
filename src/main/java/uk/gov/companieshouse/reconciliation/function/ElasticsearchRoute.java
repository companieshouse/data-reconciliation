package uk.gov.companieshouse.reconciliation.function;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.builder.RouteBuilder;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.ArrayList;
import java.util.Collections;

@Component
public class ElasticsearchRoute extends RouteBuilder {

    @Value("${endpoint.elasticsearch.threads}")
    private int numberOfThreads;

    @Override
    public void configure() throws Exception {
        from("direct:elastic")
                .setHeader("ElasticList", constant(new ResourceList(Collections.synchronizedList(new ArrayList<>()), "Elasticsearch")))
                .setBody(header("ElasticsearchQuery"))
                .to("{{endpoint.elasticsearch.alpha}}")
                .split()
                .body()
                .streaming()
                .parallelProcessing()
                .threads(numberOfThreads, numberOfThreads)
                .aggregate((AggregationStrategy) (prev, curr) -> {
                    if (prev == null) {
                        curr.getIn().getHeader("ElasticList", ResourceList.class).add(curr.getIn().getBody(SearchHit.class).getId());
                        return curr;
                    }
                    prev.getIn().getHeader("ElasticList", ResourceList.class).add(curr.getIn().getBody(SearchHit.class).getId());
                    return prev;
                })
                .constant(true)
                .completionTimeout(5000)
                .stop();
    }
}
