package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import org.elasticsearch.search.SearchHit;

import java.util.Collection;
import java.util.Iterator;

/**
 * Constructs {@link ElasticsearchSlicedScrollRunner runners} that are used to initiate and managed a sliced scrolling
 * search session.
 */
public class ElasticsearchSlicedScrollRunnerFactory {
    public ElasticsearchSlicedScrollRunner getRunner(ElasticsearchScrollingSearchClient scrollingSearchClient, Collection<Iterator<SearchHit>> results, int sliceId, int noOfSlices, String query, ElasticsearchSlicedScrollIterator scrollService) {
        return new ElasticsearchSlicedScrollRunner(scrollingSearchClient, results, sliceId, noOfSlices, query, scrollService, new ElasticsearchSlicedScrollValidator());
    }
}
