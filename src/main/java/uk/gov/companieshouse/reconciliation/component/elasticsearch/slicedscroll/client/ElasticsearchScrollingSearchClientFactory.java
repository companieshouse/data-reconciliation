package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class ElasticsearchScrollingSearchClientFactory {

    public ElasticsearchScrollingSearchClient build(String hostname, int port, String scheme, String index, int size, long timeout) {
        return new ElasticsearchScrollingSearchClient(new RestHighLevelClient(
                RestClient.builder(
                    new HttpHost(hostname, port, scheme)
                )
        ), index, size, timeout, new ElasticsearchSlicedScrollValidator());
    }
}
