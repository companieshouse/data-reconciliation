package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll;

import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultProducer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.reconciliation.App;
import uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client.ElasticsearchScrollingSearchClient;
import uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client.ElasticsearchSlicedScrollIterator;
import uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client.ElasticsearchSlicedScrollRunnerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;

public class ElasticsearchSlicedScrollProducer extends DefaultProducer {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.APPLICATION_NAMESPACE);

    private final ElasticsearchSlicedScrollEndpoint endpoint;
    private final ElasticsearchScrollingSearchClient client;

    public ElasticsearchSlicedScrollProducer(ElasticsearchSlicedScrollEndpoint endpoint, ElasticsearchScrollingSearchClient client) {
        super(endpoint);
        this.endpoint = endpoint;
        this.client = client;
    }

    @Override
    public void process(Exchange exchange) {
        exchange.getIn().setBody(new ElasticsearchSlicedScrollIterator(client, endpoint.getNumberOfSegments(), exchange.getIn().getBody(String.class), new ElasticsearchSlicedScrollRunnerFactory(), Executors.newFixedThreadPool(endpoint.getNumberOfSegments() + 1)));
    }

    public void close() {
        try {
            this.client.close();
        } catch (IOException e) {
            LOGGER.error("Failed to close Elasticsearch client", e);
        }
    }

    public ElasticsearchScrollingSearchClient getClient() {
        return client;
    }
}
