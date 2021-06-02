package uk.gov.companieshouse.reconciliation.service.elasticsearch.primary;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.Results;
import uk.gov.companieshouse.reconciliation.service.elasticsearch.ElasticsearchTransformer;

import java.util.Iterator;

/**
 * Transform {@link SearchHit search hits} retrieved from the Elasticsearch primary index into a collection
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

    public Results transform(@Body Iterator<SearchHit> it, @Header("ElasticsearchLogIndices") Integer logIndices) {
        return resultTransformer.transform(it, logIndices, searchHitMapper);
    }
}
