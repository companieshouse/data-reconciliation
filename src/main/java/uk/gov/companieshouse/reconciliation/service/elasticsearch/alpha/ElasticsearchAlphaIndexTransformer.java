package uk.gov.companieshouse.reconciliation.service.elasticsearch.alpha;

import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.reconciliation.model.Results;
import uk.gov.companieshouse.reconciliation.service.elasticsearch.ElasticsearchTransformer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Transform {@link SearchHit search hits} retrieved from the Elasticsearch alphabetical index into a collection
 * of {@link Results results}.
 */
public class ElasticsearchAlphaIndexTransformer extends ElasticsearchTransformer {

    public ElasticsearchAlphaIndexTransformer(@Value("${results.initial.capacity}") int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    protected void addSourceFieldToNameList(List<String> names, SearchHit hit, String sourceField) {
        Optional.ofNullable(hit.getSourceAsMap().get("items"))
                .map(item -> ((Map<?,?>)item).get(sourceField))
                .map(Object::toString)
                .map(String::trim)
                .filter(nameEnding -> !nameEnding.isEmpty())
                .ifPresent(names::add);
    }
}
