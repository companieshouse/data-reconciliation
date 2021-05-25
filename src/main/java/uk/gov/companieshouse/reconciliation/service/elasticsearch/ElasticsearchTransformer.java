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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Transform {@link SearchHit search hits} retrieved from an Elasticsearch index into a collection
 * of {@link Results results}.
 */
@Component
public class ElasticsearchTransformer {

    @Value("${results.initial.capacity}")
    private int initialCapacity;

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchTransformer.class);

    /**
     * Iterate over {@link SearchHit search hits} retrieved from an Elasticsearch index and map ID and corporate body
     * name for each corporate body to a {@link Results results object} containing {@link ResultModel results models}.
     *
     * @param it A {@link java.util.Iterator iterator} from which incoming {@link SearchHit search hits} can be obtained.
     * @param logIndices The number of search indices after which a message will be printed to the logs.
     * @return A {@link Results results object} containing all results fetched from the target Elasticsearch index.
     */
    public Results transform(@Body Iterator<SearchHit> it, @Header("ElasticsearchLogIndices") Integer logIndices) {
        Results results = new Results(new HashSet<>(initialCapacity));
        while (it.hasNext()) {
            SearchHit hit = it.next();
            if (hit.hasSource()) {
                List<String> name = new ArrayList<>();
                addSourceFieldToNameList(name, hit, "corporate_name_start");
                addSourceFieldToNameList(name, hit, "corporate_name_ending");
                results.add(new ResultModel(hit.getId(), String.join(" ", name))); //id cannot be null
            } else {
                results.add(new ResultModel(hit.getId(), ""));
            }
            if (logIndices != null && results.size() % logIndices == 0) {
                LOGGER.info("Indexed {} entries", results.size());
            }
        }
        LOGGER.info("Indexed {} entries", results.size());

        return results;
    }

    private void addSourceFieldToNameList(List<String> names, SearchHit hit, String sourceField) {
        Optional.ofNullable(hit.getSourceAsMap().get("items"))
                .flatMap(items -> ((List<?>)items).stream().findFirst())
                .map(item -> ((Map<?,?>)item).get(sourceField))
                .map(Object::toString)
                .map(String::trim)
                .filter(nameEnding -> !nameEnding.isEmpty())
                .ifPresent(names::add);
    }
}
