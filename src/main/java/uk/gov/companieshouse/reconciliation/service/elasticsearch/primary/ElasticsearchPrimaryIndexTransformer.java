package uk.gov.companieshouse.reconciliation.service.elasticsearch.primary;

import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.Results;
import uk.gov.companieshouse.reconciliation.service.elasticsearch.ElasticsearchTransformer;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Transform {@link SearchHit search hits} retrieved from the Elasticsearch primary index into a collection
 * of {@link Results results}.
 */
@Component
public class ElasticsearchPrimaryIndexTransformer extends ElasticsearchTransformer {

    public ElasticsearchPrimaryIndexTransformer(@Value("${results.initial.capacity}") int initialCapacity) {
        super(initialCapacity);
    }

    protected void addSourceFieldToNameList(List<String> names, SearchHit hit, String sourceField) {
        Optional.ofNullable(hit.getSourceAsMap().get("items"))
                .flatMap(items -> ((List<?>)items).stream().findFirst())
                .map(item -> ((Map<?,?>)item).get(sourceField))
                .map(Object::toString)
                .map(String::trim)
                .filter(nameEnding -> !nameEnding.isEmpty())
                .ifPresent(names::add);
    }
}
