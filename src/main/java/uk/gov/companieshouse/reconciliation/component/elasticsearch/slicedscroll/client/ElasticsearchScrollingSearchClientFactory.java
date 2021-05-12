package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * Constructs {@link ElasticsearchScrollingSearchClient clients} used to initiate and continue Elasticsearch
 * sliced scrolling search sessions.
 */
public class ElasticsearchScrollingSearchClientFactory {

    public ElasticsearchScrollingSearchClient build(String hostname, int port, String scheme, String index, int size, long timeout, String sliceField) {
        return new ElasticsearchScrollingSearchClient(new RestHighLevelClient(
                RestClient.builder(
                    new HttpHost(hostname, port, scheme)
                )
        ), index, size, timeout, sliceField, new ElasticsearchSlicedScrollValidator());
    }
}
