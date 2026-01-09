package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;


import java.io.IOException;
import java.util.List;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.ClearScrollRequest;
import co.elastic.clients.elasticsearch.core.ClearScrollResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;

/**
 * Retrieves search hits from an Elasticsearch search index using a sliced scrolling search.
 */
public class ElasticsearchScrollingSearchClient implements AutoCloseable {

    private final ElasticsearchClient client;
    private final String index;
    private final ElasticsearchSlicedScrollValidator validator;

    public ElasticsearchScrollingSearchClient(ElasticsearchClient client, String index, ElasticsearchSlicedScrollValidator validator) {
        this.client = client;
        this.index = index;
        this.validator = validator;
    }

    /**
     * Initiates a new sliced scrolling search session.
     *
     * @param query      A JSON entity used to control both the query that will be executed against the search index and the
     *                   source fields that should be returned in the response.
     * @param sliceId    The id of the sliced scrolling search session that will be created.
     * @param noOfSlices The total number of slices that will be created.
     * @return A {@link SearchResponse search response instance} containing search hits returned by the index.
     * @throws IOException If an error is raised by Elasticsearch.
     */
    public SearchResponse<Object> firstSearch(String query, int sliceId, int noOfSlices) throws IOException {
        if (!validator.validateSliceConfiguration(sliceId, noOfSlices)) {
            throw new IllegalArgumentException("Invalid client configuration [sliceId=" + sliceId + ", noOfSlices=" + noOfSlices + "]");
        }
        SearchRequest.Builder searchBuilder = new SearchRequest.Builder().index(index);
        searchBuilder.withJson(new java.io.StringReader(query));
        SearchRequest searchRequest = searchBuilder.build();
        return client.search(searchRequest, Object.class);
    }

    /**
     * Deletes open scrolling search sessions.
     *
     * @param scrollIds The IDs of the scrolling searches that should be closed.
     * @return A {@link ClearScrollResponse response} indicating the result of the operation.
     * @throws IOException If an error is raised by Elasticsearch.
     */
    public ClearScrollResponse clearScroll(List<String> scrollIds) throws IOException {
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest.Builder().scrollId(scrollIds).build();
        return client.clearScroll(clearScrollRequest);
    }

    @Override
    public void close() throws IOException {
        // No explicit close needed for ElasticsearchClient
    }
}
