package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import java.util.HashSet;
import java.util.Iterator;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.reconciliation.App;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

/**
 * Transform {@link SearchHit search hits} retrieved from an Elasticsearch index into a collection
 * of {@link Results results}.
 */
@Component
public class ElasticsearchTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.APPLICATION_NAMESPACE);
    private int initialCapacity;

    public ElasticsearchTransformer(@Value("${results.initial.capacity}") int initialCapacity) {
        this.initialCapacity = initialCapacity;
    }

    /**
     * Iterate over {@link SearchHit search hits} retrieved from an Elasticsearch index and map ID
     * and corporate body name for each corporate body to a {@link Results results object}
     * containing {@link ResultModel results models}.
     *
     * @param it         A {@link java.util.Iterator iterator} from which incoming
     *                   {@link SearchHit search hits} can be obtained.
     * @param logIndices The number of search indices after which a message will be printed to the
     *                   logs.
     * @return A {@link Results results object} containing all results fetched from the target
     * Elasticsearch index.
     */
    public Results transform(Iterator<SearchHit> it, Integer logIndices,
            ElasticsearchResultMappable resultMapper) {
        Results results = new Results(new HashSet<>(initialCapacity));
        while (it.hasNext()) {
            SearchHit hit = it.next();
            if (hit.hasSource()) {
                results.add(resultMapper.mapWithSourceFields(hit));
            } else {
                results.add(resultMapper.mapExcludingSourceFields(hit));
            }
            if (logIndices != null && results.size() % logIndices == 0) {
                LOGGER.info("Indexed %d entries".formatted(results.size()));
            }
        }
        LOGGER.info("Indexed %d entries".formatted(results.size()));

        return results;
    }
}
