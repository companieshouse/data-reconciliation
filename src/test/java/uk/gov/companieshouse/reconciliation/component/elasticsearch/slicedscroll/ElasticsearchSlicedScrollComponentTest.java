package uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client.ElasticsearchSlicedScrollIterator;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ElasticsearchSlicedScrollComponentTest {

    private ElasticsearchSlicedScrollComponent component;

    private CamelContext context;

    @BeforeEach
    void setUp() {
        this.component = new ElasticsearchSlicedScrollComponent();
        this.context = new DefaultCamelContext();
    }

    @Test
    void testCreateEndpointAndProducer() throws Exception {
        //given
        Exchange exchange = new DefaultExchange(context);
        component.setCamelContext(context);

        //when
        ElasticsearchSlicedScrollEndpoint endpoint = component.createEndpoint("es-bulk-load://endpoint" +
                "?hostname=localhost" +
                "&indexName=indexName" +
                "&portNumber=80" +
                "&protocol=http" +
                "&numberOfSegments=5" +
                "&maximumSliceSize=250" +
                "&timeoutInSeconds=30",
                "endpoint",
                new HashMap<String, Object>(){{
                    put("hostname", "localhost");
                    put("indexName", "indexName");
                    put("portNumber", "80");
                    put("protocol", "http");
                    put("numberOfSegments", "5");
                    put("maximumSliceSize", "250");
                    put("timeoutInSeconds", "30");
                }});
        Producer producer = endpoint.createProducer();
        producer.process(exchange);
        producer.close();

        //then
        assertEquals("localhost", endpoint.getHostname());
        assertEquals("indexName", endpoint.getIndexName());
        assertEquals(80, endpoint.getPortNumber());
        assertEquals("http", endpoint.getProtocol());
        assertEquals(5, endpoint.getNumberOfSegments());
        assertEquals(250, endpoint.getMaximumSliceSize());
        assertEquals(30L, endpoint.getTimeoutInSeconds());
        assertEquals(endpoint, producer.getEndpoint());
        assertNotNull(exchange.getIn().getBody(ElasticsearchSlicedScrollIterator.class));
    }
}
