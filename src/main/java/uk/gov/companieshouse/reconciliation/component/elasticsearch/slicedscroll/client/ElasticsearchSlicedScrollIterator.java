package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import org.elasticsearch.search.SearchHit;

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
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.reconciliation.App;

/**
 * All {@link SearchHit search hits} returned by an Elasticsearch sliced scrolling search.
 */
public class ElasticsearchSlicedScrollIterator implements Runnable, Iterator<SearchHit> {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.APPLICATION_NAMESPACE);

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
                throw new ElasticsearchException("Failed to retrieve results from Elasticsearch");
            }
            if (current == null || !current.hasNext()) {
                current = hits.poll();
            }
            return current != null && current.hasNext();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ElasticsearchException(e);
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
            LOGGER.error(e);
            // Temporary (?) workaround to still send elasticsearch emails in the event of an exception
            // 404 responses from attempts to delete a scroll ID are incorrectly (?) interpreted as exceptions
//            this.completedExceptionally = true;
//            futures.forEach(f -> f.cancel(true));
//            throw new ElasticsearchException(e);
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
                LOGGER.error("Error clearing scrolling search", e);
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
