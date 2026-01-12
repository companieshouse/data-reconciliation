package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import co.elastic.clients.elasticsearch.core.search.Hit;
import java.util.Iterator;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

/**
 * Transform {@link Hit search hits} retrieved from an Elasticsearch index into a collection
 * of {@link Results results}.
 */
@Component
public class ElasticsearchTransformer {

    /**
     * Iterate over {@link Hit search hits} retrieved from an Elasticsearch index and map ID
     * and source fields to a {@link ResultModel result model}.
     *
     * @param it An iterator from which {@link Hit search hits} can be obtained.
     * @param mappingFunction The mapping function.
     * @param includeSourceFields Flag indicating whether to include source fields in the mapping.
     * @return A {@link Results results object} containing mapped result models.
     */
    public Results transform(Iterator<Hit<Object>> it, ElasticsearchResultMappable mappingFunction, boolean includeSourceFields) {
        Results results = new Results(new java.util.ArrayList<>());
        while (it.hasNext()) {
            Hit<Object> hit = it.next();
            ResultModel resultModel = includeSourceFields
                ? mappingFunction.mapWithSourceFields(hit)
                : mappingFunction.mapExcludingSourceFields(hit);
            results.add(resultModel);
        }
        return results;
    }
}
