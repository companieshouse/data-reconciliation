package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

/**
 * A client or server error has occured when fetching results from Elasticsearch.
 */
public class ElasticsearchException extends RuntimeException {

    public ElasticsearchException(String message) {
        super(message);
    }

    public ElasticsearchException(Throwable cause) {
        super(cause);
    }

    public ElasticsearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
