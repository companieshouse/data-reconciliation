package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * All {@link SearchHit search hits} returned by an Elasticsearch sliced scrolling search.
 */
public class ElasticsearchSlicedScrollIterator implements Runnable, Iterator<SearchHit> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchSlicedScrollIterator.class);

    private final ElasticsearchScrollingSearchClient client;
    private final int noOfSlices;
    private final String query;
    private final ElasticsearchSlicedScrollRunnerFactory runnerFactory;
    private final ExecutorService executorService;

    private final Deque<Iterator<SearchHit>> hits;
    private Iterator<SearchHit> current;
    private volatile boolean running;
    private volatile boolean done;
    private volatile boolean completedExceptionally;

    public ElasticsearchSlicedScrollIterator(ElasticsearchScrollingSearchClient client, int noOfSlices, String query, ElasticsearchSlicedScrollRunnerFactory runnerFactory, ExecutorService executorService) {
        this(client, noOfSlices, query, runnerFactory, executorService, new LinkedBlockingDeque<>());
    }

    public ElasticsearchSlicedScrollIterator(ElasticsearchScrollingSearchClient client, int noOfSlices, String query, ElasticsearchSlicedScrollRunnerFactory runnerFactory, ExecutorService executorService, Deque<Iterator<SearchHit>> hits) {
        this.client = client;
        this.noOfSlices = noOfSlices;
        this.query = query;
        this.runnerFactory = runnerFactory;
        this.executorService = executorService;
        this.hits = hits;
        this.current = null;
    }

    @Override
    public boolean hasNext() {
        try {
            synchronized (this) {
                if (!running) {
                    executorService.execute(this);
                    this.running = true;
                }
                if (isDone() && (current == null || !current.hasNext()) && hits.isEmpty()) {
                    return false;
                } else if ((current == null || !current.hasNext()) && hits.isEmpty()) {
                    while (!done && hits.isEmpty()) {
                        wait();
                    }
                }
            }
            if(completedExceptionally) {
                throw new RuntimeException("Failed to retrieve results from Elasticsearch");
            }
            if (current == null || !current.hasNext()) {
                current = hits.poll();
            }
            return current != null && current.hasNext();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Override
    public SearchHit next() {
        if(current != null && current.hasNext()) {
            return current.next();
        }
        throw new NoSuchElementException("No further search hits found");
    }

    @Override
    public void run() {
        List<ElasticsearchSlicedScrollRunner> runners = IntStream.range(0, noOfSlices)
                .mapToObj(sliceId -> runnerFactory.getRunner(client, hits, sliceId, noOfSlices, query, this))
                .collect(Collectors.toList());
        List<CompletableFuture<?>> futures = new ArrayList<>();
        try {
            runners.stream()
                    .map(e -> CompletableFuture.runAsync(e, executorService))
                    .forEach(futures::add);
            CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join();
        } catch (CompletionException e) {
            this.completedExceptionally = true;
            futures.forEach(f -> f.cancel(true));
            throw new RuntimeException(e);
        } finally {
            try {
                List<String> scrollIdsToClear = runners.stream()
                        .map(ElasticsearchSlicedScrollRunner::getScrollId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                if(!scrollIdsToClear.isEmpty()) {
                    client.clearScroll(scrollIdsToClear);
                }
            } catch (IOException e) {
                LOGGER.warn("Error clearing scrolling search", e);
            } finally {
                synchronized (this) {
                    this.executorService.shutdown();
                    this.done = true;
                    this.notifyAll();
                }
            }
        }
    }

    public boolean isDone() {
        return done;
    }
}
