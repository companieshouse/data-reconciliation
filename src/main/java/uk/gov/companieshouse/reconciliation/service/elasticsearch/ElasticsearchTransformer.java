package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.HashSet;
import java.util.Iterator;

@Component
public class ElasticsearchTransformer {

    @Value("${results.initial.capacity}")
    private int initialCapacity;

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchTransformer.class);

    public Results transform(@Body Iterator<SearchHit> it, @Header("ElasticsearchLogIndices") Integer logIndices) {
        Results results = new Results(new HashSet<>(initialCapacity));
        while (it.hasNext()) {
            SearchHit hit = it.next();
            results.add(new ResultModel(hit.getId(), (String)hit.getSourceAsMap().get("corporate_name_start") + (String)hit.getSourceAsMap().get("corporate_name_end"))); //id cannot be null
            if (logIndices != null && results.size() % logIndices == 0) {
                LOGGER.info("Indexed {} entries", results.size());
            }
        }
        LOGGER.info("Indexed {} entries", results.size());

        return results;
    }
}
