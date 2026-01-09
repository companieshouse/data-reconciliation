package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;

import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;

/**
 * Initiates and manages a single slice of a scrolling search session.
 */
public class ElasticsearchSlicedScrollRunner implements Runnable {

    private final ElasticsearchScrollingSearchClient scrollingSearchClient;
    private final int sliceId;
    private final int noOfSlices;
    private final String query;
    private final ElasticsearchSlicedScrollIterator scrollService;
    private final ElasticsearchSlicedScrollValidator validator;

    private final Deque<Iterator<Hit<Object>>> results;

    public ElasticsearchSlicedScrollRunner(ElasticsearchScrollingSearchClient scrollingSearchClient, Deque<Iterator<Hit<Object>>> results, int sliceId, int noOfSlices, String query, ElasticsearchSlicedScrollIterator scrollService, ElasticsearchSlicedScrollValidator validator) {
        this.scrollingSearchClient = scrollingSearchClient;
        this.results = results;
        this.sliceId = sliceId;
        this.noOfSlices = noOfSlices;
        this.query = query;
        this.scrollService = scrollService;
        this.validator = validator;
    }

    public void run() {
        if (!this.validator.validateSliceConfiguration(sliceId, noOfSlices)) {
            throw new IllegalStateException("Invalid runner configuration " +
                    "[sliceId=" + sliceId + ", noOfSlices=" + noOfSlices + "]");
        }
        try {
            firstSearch();
        } catch (Exception e) {
            throw new ElasticsearchException(e);
        }
    }

    private void firstSearch() throws IOException {
        SearchResponse<Object> searchResponse = scrollingSearchClient.firstSearch(query, sliceId, noOfSlices);
        if (searchResponse.hits().hits() == null || searchResponse.hits().hits().isEmpty()) {
            return;
        }
        results.add(searchResponse.hits().hits().iterator());
        synchronized (scrollService) {
            scrollService.notify();
        }
    }
}
