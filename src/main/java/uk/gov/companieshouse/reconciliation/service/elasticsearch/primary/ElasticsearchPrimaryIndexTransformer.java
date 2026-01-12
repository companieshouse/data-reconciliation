package uk.gov.companieshouse.reconciliation.service.elasticsearch.primary;

import co.elastic.clients.elasticsearch.core.search.Hit;
import org.apache.camel.Body;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.Results;
import uk.gov.companieshouse.reconciliation.service.elasticsearch.ElasticsearchTransformer;

import java.util.Iterator;

/**
 * Transform {@link Hit search hits} retrieved from the Elasticsearch primary index into a collection
 * of {@link Results results}.
 */
@Component
public class ElasticsearchPrimaryIndexTransformer {

    private final ElasticsearchTransformer resultTransformer;

    private final ElasticsearchPrimaryIndexResultMapper searchHitMapper;

    @Autowired
    public ElasticsearchPrimaryIndexTransformer(ElasticsearchTransformer resultTransformer, ElasticsearchPrimaryIndexResultMapper searchHitMapper) {
        this.resultTransformer = resultTransformer;
        this.searchHitMapper = searchHitMapper;
    }

    public Results transform(@Body Iterator<Hit<Object>> it) {
        return resultTransformer.transform(it, searchHitMapper, true); // pass true to include source fields
    }
}
