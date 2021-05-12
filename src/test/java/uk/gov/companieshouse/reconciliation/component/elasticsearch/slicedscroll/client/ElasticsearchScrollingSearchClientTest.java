package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ElasticsearchScrollingSearchClientTest {

    private static final String QUERY_MATCH_ALL = "{\"query\": {\"match_all\":{}}}";
    private static final String SCROLL_ID = "F00DFACE";

    @Mock
    private RestHighLevelClient restHighLevelClient;

    @Mock
    private SearchResponse expectedResponse;

    @Mock
    private ClearScrollResponse expectedClearScrollResponse;

    @Mock
    private ElasticsearchSlicedScrollValidator validator;

    private ElasticsearchScrollingSearchClient client;

    @BeforeEach
    void setUp() {
        client = new ElasticsearchScrollingSearchClient(restHighLevelClient, "index", 500, 30L, validator);
    }

    @Test
    void testFirstSearch() throws IOException {
        //given
        when(restHighLevelClient.search(any())).thenReturn(expectedResponse);
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(true);

        //when
        SearchResponse actual = client.firstSearch(QUERY_MATCH_ALL, 0, 2);

        //then
        assertEquals(expectedResponse, actual);
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
        when(restHighLevelClient.search(any())).thenThrow(IOException.class);
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(true);

        //when
        Executable actual = () -> client.firstSearch(QUERY_MATCH_ALL, 0, 2);

        //then
        assertThrows(IOException.class, actual);
    }

    @Test
    void testScroll() throws IOException {
        //given
        when(restHighLevelClient.searchScroll(any())).thenReturn(expectedResponse);

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
        when(restHighLevelClient.clearScroll(any())).thenReturn(expectedClearScrollResponse);

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
