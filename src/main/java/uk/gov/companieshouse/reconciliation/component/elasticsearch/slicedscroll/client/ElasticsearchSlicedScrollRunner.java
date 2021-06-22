package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import java.io.IOException;
import java.util.Collection;
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

    private final Collection<Iterator<SearchHit>> results;
    private boolean done;
    private String scrollId;

    public ElasticsearchSlicedScrollRunner(ElasticsearchScrollingSearchClient scrollingSearchClient, Collection<Iterator<SearchHit>> results, int sliceId, int noOfSlices, String query, ElasticsearchSlicedScrollIterator scrollService, ElasticsearchSlicedScrollValidator validator) {
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
            if (scrollId == null) {
                return;
            }
            while (!done) {
                scrollSearch();
            }
        } catch (IOException e) {
            throw new ElasticsearchException(e);
        }
    }

    public String getScrollId() {
        return scrollId;
    }

    private void firstSearch() throws IOException {
        SearchResponse searchResponse = scrollingSearchClient.firstSearch(query, sliceId, noOfSlices);
        if (searchResponse.getHits().getHits() == null || searchResponse.getHits().getHits().length == 0) {
            done = true;
            return;
        }
        results.add(searchResponse.getHits().iterator());
        synchronized (scrollService) {
            scrollService.notify();
        }
        this.scrollId = searchResponse.getScrollId();
    }

    private void scrollSearch() throws IOException {
        SearchResponse response = scrollingSearchClient.scroll(scrollId);
        if (response.getHits().getHits() != null && response.getHits().getHits().length > 0) {
            results.add(response.getHits().iterator());
            synchronized (scrollService) {
                scrollService.notify();
            }
        } else {
            done = true;
        }
    }
}
