package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Retrieves and aggregates results from a mongo.collection.distinct query.<br>
 * <br>
 * IN:<br>
 * <br>
 * header(MongoEndpoint): The endpoint from which results will be fetched.<br>
 * header(MongoDescription): A description of the {@link ResourceList resource list} that will be aggregated.<br>
 * header(MongoTargetHeader): The header in which results will be aggregated as a {@link ResourceList resource list}.<br>
 */
@Component
public class MongoCollectionRoute extends RouteBuilder {

    @Value("${endpoint.mongodb.threads}")
    private int numberOfThreads;

    @Override
    public void configure() throws Exception {
        from("direct:mongodb-collection")
                .toD("${header.MongoEndpoint}")
                .split()
                .body()
                .aggregationStrategy((prev, curr) -> {
                    String result = curr.getIn().getBody(String.class);
                    if(prev == null) {
                        ResourceList resourceList = new ResourceList(Collections.synchronizedList(new LinkedList<>()), curr.getIn().getHeader("MongoDescription", String.class));
                        resourceList.add(result);
                        curr.getIn().setHeader(curr.getIn().getHeader("MongoTargetHeader", String.class), resourceList);
                        return curr;
                    }
                    ResourceList resourceList = prev.getIn().getHeader(prev.getIn().getHeader("MongoTargetHeader", String.class), ResourceList.class);
                    resourceList.add(result);
                    return prev;
                })
                .process();
    }
}
