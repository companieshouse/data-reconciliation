package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.AfterEach;
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
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
class ElasticsearchScrollingSearchClientTest {

    private static final String QUERY_MATCH_ALL = "{\"query\": {\"match_all\":{}}}";
    private static final String SCROLL_ID = "F00DFACE";
    private static final String SLICE_FIELD = "_uid";
    private static final String MINIMAL_VALID_QUERY = "{}";

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
        try {
            lenient().when(restHighLevelClient.search(any(), any())).thenReturn(expectedResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
        client = null;
    }

    @Test
    void testFirstSearchMultipleSlices() throws IOException {
        //given
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(true);
        //when
        SearchResponse actual = client.firstSearch(MINIMAL_VALID_QUERY, 0, 2);
        //then
        assertEquals(expectedResponse, actual);
        verify(restHighLevelClient).search(request.capture(), eq(RequestOptions.DEFAULT));
        SearchRequest req = request.getValue();
        assertNotNull(req.source().slice());
    }

    @Test
    void testFirstSearchSingleSlice() throws IOException {
        //given
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(true);
        //when
        SearchResponse actual = client.firstSearch(MINIMAL_VALID_QUERY, 0, 1);
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
        Executable actual = () -> client.firstSearch(QUERY_MATCH_ALL, 2, 2);

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
        Executable actual = () -> client.firstSearch(MINIMAL_VALID_QUERY, 0, 2);
        //then
        assertThrows(IOException.class, actual);
    }

    @Test
    void testScroll() throws IOException {
        //given
        ArgumentCaptor<org.elasticsearch.action.search.SearchScrollRequest> scrollRequestCaptor = ArgumentCaptor.forClass(org.elasticsearch.action.search.SearchScrollRequest.class);
        when(restHighLevelClient.scroll(any(org.elasticsearch.action.search.SearchScrollRequest.class), eq(RequestOptions.DEFAULT))).thenReturn(expectedResponse);
        //when
        SearchResponse actual = client.scroll(SCROLL_ID);
        //then
        assertSame(expectedResponse, actual);
        verify(restHighLevelClient).scroll(scrollRequestCaptor.capture(), eq(RequestOptions.DEFAULT));
        assertEquals(SCROLL_ID, scrollRequestCaptor.getValue().scrollId());
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
}

// Add a separate test class for close()
@ExtendWith(MockitoExtension.class)
class ElasticsearchScrollingSearchClientCloseTest {
    @Mock
    private RestHighLevelClient restHighLevelClient;
    @Mock
    private ElasticsearchSlicedScrollValidator validator;
    private ElasticsearchScrollingSearchClient client;
    @BeforeEach
    void setUp() {
        client = new ElasticsearchScrollingSearchClient(restHighLevelClient, "index", 500, 30L, "_uid", validator);
    }
    @Test
    void testClose() throws IOException {
        client.close();
        verify(restHighLevelClient).close();
    }
}
