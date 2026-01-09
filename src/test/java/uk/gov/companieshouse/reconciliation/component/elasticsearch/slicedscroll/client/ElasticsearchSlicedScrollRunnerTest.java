package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElasticsearchSlicedScrollRunnerTest {
    private static final String QUERY_MATCH_ALL = "{\"query\": {\"match_all\": {}}}";

    @Mock
    private ElasticsearchScrollingSearchClient client;

    private Deque<Iterator<Hit<Object>>> results;

    @Mock
    private ElasticsearchSlicedScrollIterator scrollService;

    @Mock
    private ElasticsearchSlicedScrollValidator validator;

    @Mock
    private SearchResponse<Object> response, nextResponse;

    @BeforeEach
    void setUp() {
        this.results = new LinkedList<>();
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
        when(response.hits()).thenReturn(new HitsMetadata.Builder<>().hits(new ArrayList<>()).build());

        //when
        Executable actual = runner::run;

        //then
        assertDoesNotThrow(actual);
        verify(client).firstSearch(QUERY_MATCH_ALL, 0, 2);
        assertEquals(0, results.size());
    }

    @Test
    void testResultsOnFirstSearchResultsOnSecondSearch() throws IOException {
        //given
        ElasticsearchSlicedScrollRunner runner = new ElasticsearchSlicedScrollRunner(client, results, 0, 2, QUERY_MATCH_ALL, scrollService, validator);
        when(validator.validateSliceConfiguration(anyInt(), anyInt())).thenReturn(true);
        when(client.firstSearch(anyString(), anyInt(), anyInt())).thenReturn(response, nextResponse);
        Hit<Object> hit = Hit.of(b -> b.id("id1"));
        ArrayList<Hit<Object>> firstHits = new ArrayList<>();
        firstHits.add(hit);
        when(response.hits()).thenReturn(new HitsMetadata.Builder<>().hits(firstHits).build());
        ArrayList<Hit<Object>> secondHits = new ArrayList<>();
        secondHits.add(hit);
        when(nextResponse.hits()).thenReturn(new HitsMetadata.Builder<>().hits(secondHits).build());

        //when
        Executable actual = runner::run;

        //then
        assertDoesNotThrow(actual);
        verify(client).firstSearch(QUERY_MATCH_ALL, 0, 2);
        assertEquals(2, results.size());
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
