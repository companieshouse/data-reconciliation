package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.search.SearchModule;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.slice.SliceBuilder;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ElasticsearchScrollingSearchClient implements AutoCloseable {

    private final RestHighLevelClient client;
    private final String index;
    private final int size;
    private final long timeout;
    private final ElasticsearchSlicedScrollValidator validator;

    public ElasticsearchScrollingSearchClient(RestHighLevelClient client, String index, int size, long timeout, ElasticsearchSlicedScrollValidator validator) {
        this.client = client;
        this.index = index;
        this.size = size;
        this.timeout = timeout;
        this.validator = validator;
    }

    public SearchResponse firstSearch(String query, int sliceId, int noOfSlices) throws IOException {
        if(!validator.validateSliceConfiguration(sliceId, noOfSlices)) {
            throw new IllegalArgumentException("Invalid client configuration [sliceId=" + sliceId + ", noOfSlices=" + noOfSlices + "]");
        }
        SearchRequest searchRequest = new SearchRequest(index);
        SearchModule module = new SearchModule(Settings.EMPTY, false, Collections.emptyList());
        XContentParser parser = JsonXContent.jsonXContent.createParser(new NamedXContentRegistry(module.getNamedXContents()), query);
        SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.fromXContent(parser);
        searchRequest.scroll(new TimeValue(timeout, TimeUnit.SECONDS))
                .source()
                .query(searchSourceBuilder.query())
                .slice(new SliceBuilder(sliceId, noOfSlices))
                .fetchSource(searchSourceBuilder.fetchSource())
                .size(size);
        return client.search(searchRequest);
    }

    public SearchResponse scroll(String scrollId) throws IOException {
        if(scrollId == null) {
            throw new IllegalArgumentException("Scroll ID is null");
        } else if(scrollId.isEmpty()) {
            throw new IllegalArgumentException("Scroll ID is empty");
        }
        SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
        searchScrollRequest.scroll(TimeValue.timeValueSeconds(timeout));
        return client.searchScroll(searchScrollRequest);
    }

    public ClearScrollResponse clearScroll(List<String> scrollIds) throws IOException {
        ClearScrollRequest clearScrollRequest = new ClearScrollRequest();
        clearScrollRequest.setScrollIds(scrollIds);
        return client.clearScroll(clearScrollRequest);
    }

    @Override
    public void close() throws IOException {
        this.client.close();
    }
}
