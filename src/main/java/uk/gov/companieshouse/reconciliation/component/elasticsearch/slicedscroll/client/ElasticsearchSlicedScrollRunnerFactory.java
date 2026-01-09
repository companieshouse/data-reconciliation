package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import co.elastic.clients.elasticsearch.core.search.Hit;

import java.util.Deque;
import java.util.Iterator;

/**
 * Constructs {@link ElasticsearchSlicedScrollRunner runners} that are used to initiate and managed a sliced scrolling
 * search session.
 */
public class ElasticsearchSlicedScrollRunnerFactory {
    public ElasticsearchSlicedScrollRunner getRunner(ElasticsearchScrollingSearchClient scrollingSearchClient, Deque<Iterator<Hit<Object>>> results, int sliceId, int noOfSlices, String query, ElasticsearchSlicedScrollIterator scrollService) {
        return new ElasticsearchSlicedScrollRunner(scrollingSearchClient, results, sliceId, noOfSlices, query, scrollService, new ElasticsearchSlicedScrollValidator());
    }
}
