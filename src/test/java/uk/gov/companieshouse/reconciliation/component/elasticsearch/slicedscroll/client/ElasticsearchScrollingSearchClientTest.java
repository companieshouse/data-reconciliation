package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.ClearScrollRequest;
import co.elastic.clients.elasticsearch.core.ClearScrollResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElasticsearchScrollingSearchClientTest {

    private static final String QUERY_MATCH_ALL = "{\"query\": {\"match_all\":{}}}";
    private static final String SCROLL_ID = "F00DFACE";

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @Mock
    private SearchResponse<Object> expectedResponse;

    @Mock
    private ClearScrollResponse expectedClearScrollResponse;

    @Mock
    private ElasticsearchSlicedScrollValidator validator;

    @Captor
    private ArgumentCaptor<SearchRequest> request;

    private ElasticsearchScrollingSearchClient client;

    @BeforeEach
    void setUp() {
        client = new ElasticsearchScrollingSearchClient(elasticsearchClient, "index", validator);
    }

    @Test
    void testFirstSearchMultipleSlices() throws IOException {
        //given
        when(elasticsearchClient.search(any(SearchRequest.class), any())).thenReturn(expectedResponse);
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(true);

        //when
        client.firstSearch(QUERY_MATCH_ALL, 0, 2);

        //then
        verify(elasticsearchClient).search(request.capture(), any());
        SearchRequest req = request.getValue();
        assertNotNull(req);
    }

    @Test
    void testFirstSearchSingleSlice() throws IOException {
        //given
        when(elasticsearchClient.search(any(SearchRequest.class), any())).thenReturn(expectedResponse);
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(true);

        //when
        client.firstSearch(QUERY_MATCH_ALL, 0, 1);

        //then
        verify(elasticsearchClient).search(request.capture(), any());
        SearchRequest req = request.getValue();
        assertNotNull(req);
    }

    @Test
    void testFirstSearchThrowsIllegalArgumentExceptionIfInvalid() {
        //given
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(false);

        //when
        Executable actual = () -> client.firstSearch(QUERY_MATCH_ALL, 2, 2);

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        assertEquals("Invalid client configuration [sliceId=2, noOfSlices=2]", exception.getMessage());
        verifyNoInteractions(elasticsearchClient);
    }

    @Test
    void testFirstSearchPropagatesIOExceptionThrownByClient() throws IOException {
        //given
        when(elasticsearchClient.search(any(SearchRequest.class), any())).thenThrow(IOException.class);
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(true);

        //when
        Executable actual = () -> client.firstSearch(QUERY_MATCH_ALL, 0, 2);

        //then
        assertThrows(IOException.class, actual);
    }

    @Test
    void testClearScroll() throws IOException {
        //given
        when(elasticsearchClient.clearScroll(any(ClearScrollRequest.class))).thenReturn(expectedClearScrollResponse);

        //when
        ClearScrollResponse actual = client.clearScroll(Collections.singletonList(SCROLL_ID));

        //then
        assertEquals(expectedClearScrollResponse, actual);
    }

    @Test
    void testClose() {
        //when
        assertDoesNotThrow(() -> client.close());
    }
}
