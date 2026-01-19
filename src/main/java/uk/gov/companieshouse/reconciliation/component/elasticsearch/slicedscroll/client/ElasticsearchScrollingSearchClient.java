package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.slice.SliceBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Retrieves search hits from an Elasticsearch search index using a sliced scrolling search.
 */
public class ElasticsearchScrollingSearchClient implements AutoCloseable {

    private final RestHighLevelClient client;
    private final String index;
    private final int size;
    private final long timeout;
    private final String sliceField;
    private final ElasticsearchSlicedScrollValidator validator;

    public ElasticsearchScrollingSearchClient(RestHighLevelClient client, String index, int size, long timeout, String sliceField, ElasticsearchSlicedScrollValidator validator) {
        this.client = client;
        this.index = index;
        this.size = size;
        this.timeout = timeout;
        this.sliceField = sliceField;
        this.validator = validator;
    }

    /**
     * Initiates a new sliced scrolling search session.
     *
     * @param query    A JSON string representing the Elasticsearch query DSL, including optional source fields to return.
     *                 Example: '{"query": {"match_all": {}}}'
     * @param sliceId  The zero-based ID of the slice for this search session. Used for parallelizing scrolls.
     * @param noOfSlices The total number of slices to split the search into. If 1, no slicing is applied.
     * @return A {@link SearchResponse} containing the initial batch of search hits for the given slice and query.
     * @throws IOException If an error is raised by Elasticsearch or the query is invalid.
     * @throws IllegalArgumentException If the slice configuration is invalid.
     *
     * <p>
     * This method sets up a scroll context on the Elasticsearch index and returns the first page of results.
     * If slicing is enabled (noOfSlices > 1), only the results for the specified sliceId are returned.
     * The scroll context can be continued using the scroll(String scrollId) method.
     * </p>
     */
    public SearchResponse firstSearch(String query, int sliceId, int noOfSlices) throws IOException {
        if(!validator.validateSliceConfiguration(sliceId, noOfSlices)) {
            throw new IllegalArgumentException("Invalid client configuration [sliceId=" + sliceId + ", noOfSlices=" + noOfSlices + "]");
        }
        SearchRequest searchRequest = new SearchRequest(index);
        XContentParser parser = XContentType.JSON.xContent().createParser(
                NamedXContentRegistry.EMPTY,
                DeprecationHandler.THROW_UNSUPPORTED_OPERATION,
                query.getBytes(StandardCharsets.UTF_8)
        );
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.fromXContent(parser);
        SearchSourceBuilder scrollRequestSource = searchRequest.scroll(new TimeValue(timeout, TimeUnit.SECONDS))
                .source()
                .query(searchSourceBuilder.query())
                .fetchSource(searchSourceBuilder.fetchSource())
                .size(size);
        if(noOfSlices > 1) {
            scrollRequestSource.slice(new SliceBuilder(sliceField, sliceId, noOfSlices));
        }
        return client.search(searchRequest, RequestOptions.DEFAULT);
    }

    /**
     * Retrieves further results using the scroll ID of the scrolling search session that was initiated.
     *
     * @param scrollId The scroll ID that was created during the first search.
     * @return A {@link SearchResponse search response instance} containing further search hits returned by the index.
     * @throws IOException If an error is raised by Elasticsearch.
     */
    public SearchResponse scroll(String scrollId) throws IOException {
        if(scrollId == null) {
            throw new IllegalArgumentException("Scroll ID is null");
        } else if(scrollId.isEmpty()) {
            throw new IllegalArgumentException("Scroll ID is empty");
        }
        SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
        searchScrollRequest.scroll(TimeValue.timeValueSeconds(timeout));
        return client.scroll(searchScrollRequest, RequestOptions.DEFAULT);
    }

    /**
     * Deletes open scrolling search sessions.
     *
     * @param scrollIds The IDs of the scrolling searches that should be closed.
     * @return A {@link ClearScrollResponse response} indicating the result of the operation.
     * @throws IOException If an error is raised by Elasticsearch.
     */
    public ClearScrollResponse clearScroll(List<String> scrollIds) throws IOException {
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.setScrollIds(scrollIds);
        return client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT );
    }

    @Override
    public void close() throws IOException {
        this.client.close();
    }
}
