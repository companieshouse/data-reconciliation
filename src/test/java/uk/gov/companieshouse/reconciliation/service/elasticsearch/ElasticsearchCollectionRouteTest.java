package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class ElasticsearchCollectionRouteTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:elasticsearch-collection")
    private ProducerTemplate producer;

    @EndpointInject("mock:elasticsearch-stub")
    private MockEndpoint elasticsearchEndpoint;

    @AfterEach
    void after() {
        elasticsearchEndpoint.reset();
    }

    @Test
    void testStoreResourceListInRequiredHeader() throws InterruptedException {
        elasticsearchEndpoint.expectedBodyReceived().constant("QUERY");
        elasticsearchEndpoint.whenAnyExchangeReceived(exchange ->
            exchange.getIn().setBody(Collections.singletonList(
                    new SearchHit(123, "12345678", new Text("{}"), new HashMap<>())
            ).iterator())
        );
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeaders(getHeaders());
        Exchange actual = producer.send(exchange);
        assertEquals("Description", ((ResourceList)actual.getIn().getHeaders().get("Output")).getResultDesc());
        assertEquals("12345678", ((ResourceList)actual.getIn().getHeaders().get("Output")).getResultList().get(0));
        MockEndpoint.assertIsSatisfied(context);
    }

    private Map<String, Object> getHeaders() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("ElasticsearchTargetHeader", "Output");
        headers.put("ElasticsearchQuery", "QUERY");
        headers.put("ElasticsearchDescription", "Description");
        headers.put("ElasticsearchLogIndices", 1);
        headers.put("ElasticsearchEndpoint", "mock:elasticsearch-stub");
        return headers;
    }
}
