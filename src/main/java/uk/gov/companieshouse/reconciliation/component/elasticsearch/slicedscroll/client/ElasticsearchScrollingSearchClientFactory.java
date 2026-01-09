package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;

/**
 * Constructs {@link ElasticsearchScrollingSearchClient clients} used to initiate and continue
 * Elasticsearch sliced scrolling search sessions.
 */
public class ElasticsearchScrollingSearchClientFactory {

    public ElasticsearchScrollingSearchClient build(String hostname, int port, String scheme, String index) {
        RestClientBuilder builder = RestClient.builder(new HttpHost(hostname, port, scheme))
            .setRequestConfigCallback(
                configBuilder -> configBuilder.setConnectTimeout(5000).setSocketTimeout(60000*2)
            );
        RestClient restClient = builder.build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient client = new ElasticsearchClient(transport);
        return new ElasticsearchScrollingSearchClient(client, index, new ElasticsearchSlicedScrollValidator());
    }
}
