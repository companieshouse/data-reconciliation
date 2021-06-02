package uk.gov.companieshouse.reconciliation.service.elasticsearch.alpha;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.elasticsearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.Results;
import uk.gov.companieshouse.reconciliation.service.elasticsearch.ElasticsearchTransformer;

import java.util.Iterator;

/**
 * Transform {@link SearchHit search hits} retrieved from the Elasticsearch alphabetical index into a collection
 * of {@link Results results}.
 */
@Component
public class ElasticsearchAlphaIndexTransformer {

    private final ElasticsearchTransformer resultTransformer;

    private final ElasticsearchAlphaIndexResultMapper searchHitMapper;

    @Autowired
    public ElasticsearchAlphaIndexTransformer(ElasticsearchTransformer resultTransformer,
                                              ElasticsearchAlphaIndexResultMapper searchHitMapper) {
        this.resultTransformer = resultTransformer;
        this.searchHitMapper = searchHitMapper;
    }

    public Results transform(@Body Iterator<SearchHit> it, @Header("ElasticsearchLogIndices") Integer logIndices) {
        return resultTransformer.transform(it, logIndices, searchHitMapper);
    }
}
