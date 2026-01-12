package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import co.elastic.clients.elasticsearch.core.search.Hit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElasticsearchSlicedScrollIteratorTest {

    private static final String QUERY_MATCH_ALL = "{\"query\":{\"match_all\":{}}}";

    @Mock
    private ElasticsearchScrollingSearchClient client;

    @Mock
    private ElasticsearchSlicedScrollRunnerFactory factory;

    @Mock
    private ElasticsearchSlicedScrollRunner runner;

    private ElasticsearchSlicedScrollIterator iterator;
    private Deque<Iterator<Hit<Object>>> results;
    private final Object syncLock = new Object();

    @BeforeEach
    void setUp() {
        this.results = new LinkedBlockingDeque<>();
        this.iterator = new ElasticsearchSlicedScrollIterator(client, 2, QUERY_MATCH_ALL, factory, Executors.newFixedThreadPool(3), results);
    }

    @Test
    void testNoResults() {
        //given
        when(factory.getRunner(any(ElasticsearchScrollingSearchClient.class), any(), anyInt(), anyInt(), anyString(), any())).thenReturn(runner);

        //when
        boolean actual = iterator.hasNext();
        Executable nextElement = () -> iterator.next();

        //then
        assertFalse(actual);
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, nextElement);
        assertEquals("No further search hits found", exception.getMessage());
        assertEquals(0, results.size());
    }

    @Test
    void testNoFurtherResultsInIterator() throws InterruptedException {
        //given
        when(factory.getRunner(any(ElasticsearchScrollingSearchClient.class), any(), anyInt(), anyInt(), anyString(), any())).thenReturn(runner);
        doAnswer(a -> {
            synchronized (syncLock) {
                results.push(Collections.emptyIterator());
                syncLock.notify();
            }
            return null;
        }).when(runner).run();
        //when
        boolean actual = iterator.hasNext();
        Executable nextElement = () -> iterator.next();
        synchronized (syncLock) {
            while(!iterator.isDone()) {
                syncLock.wait();
            }
        }
        //then
        assertFalse(actual);
        assertThrows(NoSuchElementException.class, nextElement);
    }

    @Test
    void testFurtherResultsInIterator() throws InterruptedException {
        //given
        Hit<Object> expectedResult = Hit.of(b -> b.id("test-id").index("test-index"));
        when(factory.getRunner(any(ElasticsearchScrollingSearchClient.class), any(), anyInt(), anyInt(), anyString(), any())).thenReturn(runner);
        doAnswer(a -> {
            synchronized (syncLock) {
                results.push(Collections.singletonList(expectedResult).iterator());
                syncLock.notify();
            }
            return null;
        }).when(runner).run();
        //when
        boolean actual = iterator.hasNext();
        Hit<Object> nextElement = iterator.next();
        synchronized (syncLock) {
            while(!iterator.isDone()) {
                syncLock.wait();
            }
        }
        //then
        assertTrue(actual);
        assertEquals(expectedResult, nextElement);
    }
}
