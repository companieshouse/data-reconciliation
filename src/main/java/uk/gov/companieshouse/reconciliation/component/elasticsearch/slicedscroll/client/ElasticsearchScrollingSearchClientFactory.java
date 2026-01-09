package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.RestClientBuilder;

/**
 * Constructs {@link ElasticsearchScrollingSearchClient clients} used to initiate and continue
 * Elasticsearch sliced scrolling search sessions.
 */
public class ElasticsearchScrollingSearchClientFactory {

    public ElasticsearchScrollingSearchClient build(String hostname, int port, String scheme,
            String index, int size, long timeout, String sliceField) {
        RestClientBuilder builder = RestClient.builder(new HttpHost(hostname, port, scheme))
            .setRequestConfigCallback(
                configBuilder -> configBuilder.setConnectTimeout(5000).setSocketTimeout(60000*2)
            );
        return new ElasticsearchScrollingSearchClient(new RestHighLevelClient(builder), index, size, timeout, sliceField,
                new ElasticsearchSlicedScrollValidator());
    }
}
