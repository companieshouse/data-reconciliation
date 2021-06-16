package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ElasticsearchSlicedScrollRunnerTest {

    private static final String QUERY_MATCH_ALL = "{\"query\": {\"match_all\": {}]}";
    private static final String SCROLL_ID = "F00DFACE";

    @Mock
    private ElasticsearchScrollingSearchClient client;

    private List<Iterator<SearchHit>> results;

    @Mock
    private ElasticsearchSlicedScrollIterator scrollService;

    @Mock
    private ElasticsearchSlicedScrollValidator validator;

    @Mock
    private SearchResponse response, scrollResponse, nextScrollResponse;

    @BeforeEach
    void setUp() {
        this.results = new ArrayList<>();
    }

    @Test
    void testThrowRuntimeExceptionIfInvalid() {
        //given
        ElasticsearchSlicedScrollRunner runner = new ElasticsearchSlicedScrollRunner(client, results, 2, 2, QUERY_MATCH_ALL, scrollService, validator);
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(false);

        //when
        Executable actual = runner::run;

        //then
        IllegalStateException exception = assertThrows(IllegalStateException.class, actual);
        assertEquals("Invalid runner configuration [sliceId=2, noOfSlices=2]", exception.getMessage());
    }

    @Test
    void testRunNoResultsOnFirstSearch() throws IOException {
        //given
        ElasticsearchSlicedScrollRunner runner = new ElasticsearchSlicedScrollRunner(client, results, 0, 2, QUERY_MATCH_ALL, scrollService, validator);
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(true);
        when(client.firstSearch(anyString(), anyInt(), anyInt())).thenReturn(response);
        when(response.getHits()).thenReturn(new SearchHits(new SearchHit[0], 0, 1.0F));

        //when
        Executable actual = runner::run;

        //then
        assertDoesNotThrow(actual);
        verify(client).firstSearch(QUERY_MATCH_ALL, 0, 2);
        verify(client, times(0)).scroll(anyString());
        assertEquals(0, results.size());
    }

    @Test
    void testResultsOnFirstSearchNoResultsSecondSearch() throws IOException {
        //given
        ElasticsearchSlicedScrollRunner runner = new ElasticsearchSlicedScrollRunner(client, results, 0, 2, QUERY_MATCH_ALL, scrollService, validator);
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(true);
        when(client.firstSearch(anyString(), anyInt(), anyInt())).thenReturn(response);
        when(client.scroll(anyString())).thenReturn(scrollResponse);
        when(response.getHits()).thenReturn(new SearchHits(new SearchHit[]{new SearchHit(1)}, 1, 1.0F));
        when(response.getScrollId()).thenReturn(SCROLL_ID);
        when(scrollResponse.getHits()).thenReturn(new SearchHits(new SearchHit[0], 0, 1.0F));

        //when
        Executable actual = runner::run;

        //then
        assertDoesNotThrow(actual);
        verify(client).firstSearch(QUERY_MATCH_ALL, 0, 2);
        verify(client).scroll(SCROLL_ID);
        assertEquals(1, results.size());
        assertEquals(SCROLL_ID, runner.getScrollId());
    }

    @Test
    void testResultsOnFirstSearchResultsOnSecondSearch() throws IOException {
        //given
        ElasticsearchSlicedScrollRunner runner = new ElasticsearchSlicedScrollRunner(client, results, 0, 2, QUERY_MATCH_ALL, scrollService, validator);
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(true);
        when(client.firstSearch(anyString(), anyInt(), anyInt())).thenReturn(response);
        when(client.scroll(anyString())).thenReturn(scrollResponse, nextScrollResponse);
        when(response.getHits()).thenReturn(new SearchHits(new SearchHit[]{new SearchHit(1)}, 1, 1.0F));
        when(response.getScrollId()).thenReturn(SCROLL_ID);
        when(scrollResponse.getHits()).thenReturn(new SearchHits(new SearchHit[]{new SearchHit(1)}, 1, 1.0F));
        when(nextScrollResponse.getHits()).thenReturn(new SearchHits(new SearchHit[0], 0, 1.0F));

        //when
        Executable actual = runner::run;

        //then
        assertDoesNotThrow(actual);
        verify(client).firstSearch(QUERY_MATCH_ALL, 0, 2);
        verify(client, times(2)).scroll(SCROLL_ID);
        assertEquals(2, results.size());
        assertEquals(SCROLL_ID, runner.getScrollId());
    }

    @Test
    void testThrowElasticsearchExceptionIfIOExceptionThrown() throws IOException {
        //given
        ElasticsearchSlicedScrollRunner runner = new ElasticsearchSlicedScrollRunner(client, results, 0, 2, QUERY_MATCH_ALL, scrollService, validator);
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(true);
        when(client.firstSearch(anyString(), anyInt(), anyInt())).thenThrow(IOException.class);

        //when
        Executable actual = runner::run;

        //then
        RuntimeException exception = assertThrows(ElasticsearchException.class, actual);
        assertEquals(IOException.class, exception.getCause().getClass());
    }
}
