package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import org.elasticsearch.search.SearchHit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ElasticsearchSlicedScrollIteratorTest {

    private static final String QUERY_MATCH_ALL = "{\"query\":{\"match_all\":{}}}";

    @Mock
    private ElasticsearchScrollingSearchClient client;

    @Mock
    private ElasticsearchSlicedScrollRunnerFactory factory;

    @Mock
    private ElasticsearchSlicedScrollRunner runner;

    private ElasticsearchSlicedScrollIterator iterator;

    private Deque<Iterator<SearchHit>> results;

    @BeforeEach
    void setUp() {
        this.results = new LinkedBlockingDeque<>();
        this.iterator = new ElasticsearchSlicedScrollIterator(client, 2, QUERY_MATCH_ALL, factory, Executors.newFixedThreadPool(3), results);
    }

    @Test
    void testNoResults() throws IOException {
        //given
        when(factory.getRunner(any(ElasticsearchScrollingSearchClient.class), any(), anyInt(), anyInt(), anyString(), any())).thenReturn(runner);
        when(runner.getScrollId()).thenReturn("F00DFACE");

        //when
        boolean actual = iterator.hasNext();
        Executable nextElement = () -> iterator.next();

        //then
        assertFalse(actual);
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, nextElement);
        assertEquals("No further search hits found", exception.getMessage());
        verify(client).clearScroll(Arrays.asList("F00DFACE", "F00DFACE"));
        assertEquals(0, results.size());
    }

    @Test
    void testNoFurtherResultsInIterator() throws IOException, InterruptedException {
        //given
        when(factory.getRunner(any(ElasticsearchScrollingSearchClient.class), any(), anyInt(), anyInt(), anyString(), any())).thenReturn(runner);
        doAnswer(a -> {
            synchronized (iterator) {
                results.push(Collections.emptyIterator());
                iterator.notify();
            }
            return null;
        }).when(runner).run();
        when(runner.getScrollId()).thenReturn("F00DFACE");

        //when
        boolean actual = iterator.hasNext();
        Executable nextElement = () -> iterator.next();

        synchronized (iterator) {
            while(!iterator.isDone()) {
                iterator.wait();
            }
        }

        //then
        assertFalse(actual);
        assertThrows(NoSuchElementException.class, nextElement);
        verify(client).clearScroll(Arrays.asList("F00DFACE", "F00DFACE"));
    }

    @Test
    void testFurtherResultsInIterator() throws IOException, InterruptedException {
        //given
        SearchHit expectedResult = new SearchHit(1);
        when(factory.getRunner(any(ElasticsearchScrollingSearchClient.class), any(), anyInt(), anyInt(), anyString(), any())).thenReturn(runner);
        doAnswer(a -> {
            synchronized (iterator) {
                results.push(Collections.singletonList(expectedResult).iterator());
                iterator.notify();
            }
            return null;
        }).when(runner).run();
        when(runner.getScrollId()).thenReturn("F00DFACE");
        when(client.clearScroll(any())).thenThrow(IOException.class);

        //when
        boolean actual = iterator.hasNext();
        SearchHit nextElement = iterator.next();

        synchronized (iterator) {
            while(!iterator.isDone()) {
                iterator.wait();
            }
        }

        //then
        assertTrue(actual);
        assertEquals(expectedResult, nextElement);
        verify(client).clearScroll(Arrays.asList("F00DFACE", "F00DFACE"));
    }

    @Test
    void testThrowElasticsearchExceptionIfCompletedExceptionally() throws IOException {
        //given
        when(factory.getRunner(any(ElasticsearchScrollingSearchClient.class), any(), anyInt(), anyInt(), anyString(), any())).thenReturn(runner);
        doThrow(RuntimeException.class).when(runner).run();

        //when
        Executable actual = () -> {
            iterator.hasNext();
            synchronized (iterator) {
                while (!iterator.isDone()) {
                    iterator.wait();
                }
            }
        };

        //then
        ElasticsearchException exception = assertThrows(ElasticsearchException.class, actual);
        assertEquals("Failed to retrieve results from Elasticsearch", exception.getMessage());
        verify(client, times(0)).clearScroll(any());
    }
}
