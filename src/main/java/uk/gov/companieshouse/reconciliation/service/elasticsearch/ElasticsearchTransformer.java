package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.camel.Headers;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

@Component
public class ElasticsearchTransformer {

    @Value("${results.initial.capacity}")
    private int initialCapacity;

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchTransformer.class);

    public ResourceList transform(@Body Iterator<SearchHit> it, @Header("ElasticsearchDescription") String description,
                          @Header("ElasticsearchLogIndices") Integer logIndices) {
        ResourceList indices = new ResourceList(new HashSet<>(initialCapacity), description);
        while (it.hasNext()) {
            indices.add(it.next().getId()); //id cannot be null
            if (logIndices != null && indices.size() % logIndices == 0) {
                LOGGER.info("Indexed {} entries", indices.size());
            }
        }
        LOGGER.info("Indexed {} entries", indices.size());

        return indices;
    }
}
