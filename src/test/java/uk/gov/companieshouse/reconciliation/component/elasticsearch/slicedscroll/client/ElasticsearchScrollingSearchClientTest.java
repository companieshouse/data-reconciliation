package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElasticsearchScrollingSearchClientTest {

    private static final String SCROLL_ID = "F00DFACE";
    private static final String SLICE_FIELD = "_uid";
    private static final SearchSourceBuilder MATCH_ALL_BUILDER = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @Mock
    private SearchResponse expectedResponse;

    @Mock
    private ClearScrollResponse expectedClearScrollResponse;

    @Mock
    private ElasticsearchSlicedScrollValidator validator;

    @Captor
    private ArgumentCaptor<SearchRequest> request;

    private ElasticsearchScrollingSearchClient client;

    @BeforeEach
    void setUp() {
        client = new ElasticsearchScrollingSearchClient(restHighLevelClient, "index", 500, 30L, SLICE_FIELD, validator);
    }

    @Test
    void testFirstSearchMultipleSlices() throws IOException {
        //given
        SearchSourceBuilder matchAllBuilder = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
        when(restHighLevelClient.search(any(), any())).thenReturn(expectedResponse);
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(true);

        //when
        SearchResponse actual = client.firstSearch(matchAllBuilder, 0, 2);

        //then
        assertEquals(expectedResponse, actual);
        verify(restHighLevelClient).search(request.capture(), eq(RequestOptions.DEFAULT));
        SearchRequest req = request.getValue();
        assertNotNull(req.source().slice());
    }

    @Test
    void testFirstSearchSingleSlice() throws IOException {
        //given
        SearchSourceBuilder matchAllBuilder = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
        when(restHighLevelClient.search(any(), any())).thenReturn(expectedResponse);
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(true);

        //when
        SearchResponse actual = client.firstSearch(matchAllBuilder, 0, 1);

        //then
        assertEquals(expectedResponse, actual);
        verify(restHighLevelClient).search(request.capture(), eq(RequestOptions.DEFAULT));
        SearchRequest req = request.getValue();
        assertNull(req.source().slice());
    }

    @Test
    void testFirstSearchThrowsIllegalArgumentExceptionIfInvalid() {
        //given
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(false);

        //when
        Executable actual = () -> client.firstSearch(MATCH_ALL_BUILDER, 2, 2);

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        assertEquals("Invalid client configuration [sliceId=2, noOfSlices=2]", exception.getMessage());
        verifyNoInteractions(restHighLevelClient);
    }

    @Test
    void testFirstSearchPropagatesIOExceptionThrownByClient() throws IOException {
        //given
        when(restHighLevelClient.search(any(), any())).thenThrow(new IOException("IO error"));
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(true);

        //when
        Executable actual = () -> client.firstSearch(MATCH_ALL_BUILDER, 0, 2);

        //then
        assertThrows(IOException.class, actual);
    }

    @Test
    void testScroll() throws IOException {
        // Correctly stub the method actually called in production code
        when(restHighLevelClient.searchScroll(any(), any())).thenReturn(expectedResponse);

        //when
        SearchResponse actual = client.scroll(SCROLL_ID);

        //then
        assertEquals(expectedResponse, actual);
    }

    @Test
    void testScrollThrowsIllegalArgumentExceptionIfScrollIDEmpty() {
        //when
        Executable actual = () -> client.scroll("");

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        assertEquals("Scroll ID is empty", exception.getMessage());
    }

    @Test
    void testScrollThrowsIllegalArgumentExceptionIfScrollIDNull() {
        //when
        Executable actual = () -> client.scroll(null);

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        assertEquals("Scroll ID is null", exception.getMessage());
    }

    @Test
    void testClearScroll() throws IOException {
        //given
        when(restHighLevelClient.clearScroll(any(), any())).thenReturn(expectedClearScrollResponse);

        //when
        ClearScrollResponse actual = client.clearScroll(Collections.singletonList(SCROLL_ID));

        //then
        assertEquals(expectedClearScrollResponse, actual);
    }

    @Test
    void testClose() throws IOException {
        //when
        client.close();

        //then
        verify(restHighLevelClient).close();
    }
}
