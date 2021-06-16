package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.component.caffeine.CaffeineConstants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client.ElasticsearchException;
import uk.gov.companieshouse.reconciliation.component.elasticsearch.slicedscroll.client.ElasticsearchSlicedScrollIterator;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @EndpointInject("mock:cache")
    private MockEndpoint cache;

    @EndpointInject("mock:es-transformer")
    private MockEndpoint transformer;

    @Mock
    private ElasticsearchSlicedScrollIterator iterator;

    @AfterEach
    void after() {
        elasticsearchEndpoint.reset();
        cache.reset();
        transformer.reset();
    }

    @Test
    void testStoreResourceListInRequiredHeaderUncached() throws InterruptedException {
        elasticsearchEndpoint.expectedBodyReceived().constant("QUERY");
        elasticsearchEndpoint.whenAnyExchangeReceived(exchange ->
                exchange.getIn().setBody(iterator)
        );
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.ACTION, CaffeineConstants.ACTION_GET, CaffeineConstants.ACTION_PUT);
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.KEY, "elasticsearchCache", "elasticsearchCache");
        cache.returnReplyHeader(CaffeineConstants.ACTION_HAS_RESULT, ExpressionBuilder.constantExpression(false));
        transformer.expectedBodyReceived().constant(iterator);
        transformer.returnReplyBody(ExpressionBuilder.constantExpression(new Results(Collections.singletonList(new ResultModel("12345678", "ACME LIMITED")))));
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeaders(getHeaders());
        exchange.getIn().setBody(new Object());
        Exchange actual = producer.send(exchange);
        assertTrue(actual.getIn().getBody(Results.class).contains(new ResultModel("12345678", "ACME LIMITED")));
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void testStoreResourceListInRequiredHeaderCached() throws InterruptedException {
        elasticsearchEndpoint.expectedMessageCount(0);
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.ACTION, CaffeineConstants.ACTION_GET);
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.KEY, "elasticsearchCache");
        cache.whenAnyExchangeReceived(exchange -> {
            exchange.getIn().setBody(new Results(Collections.singletonList(new ResultModel("12345678", "ACME LIMITED"))));
            exchange.getIn().setHeader(CaffeineConstants.ACTION_HAS_RESULT, true);
        });
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeaders(getHeaders());
        exchange.getIn().setBody(new Object());
        Exchange actual = producer.send(exchange);
        assertTrue(actual.getIn().getBody(Results.class).contains(new ResultModel("12345678", "ACME LIMITED")));
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void testSetFailedHeaderIfExceptionThrownDuringScrollingSearch() throws InterruptedException {
        elasticsearchEndpoint.expectedBodyReceived().constant("QUERY");
        elasticsearchEndpoint.whenAnyExchangeReceived(exchange -> {
            throw new ElasticsearchException("Failed");
        });
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.ACTION, CaffeineConstants.ACTION_GET);
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.KEY, "elasticsearchCache");
        cache.returnReplyHeader(CaffeineConstants.ACTION_HAS_RESULT, ExpressionBuilder.constantExpression(false));
        transformer.expectedMessageCount(0);
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeaders(getHeaders());
        exchange.getIn().setBody(new Object());
        Exchange actual = producer.send(exchange);
        assertTrue(actual.getIn().getHeader("Failed", Boolean.class));
        MockEndpoint.assertIsSatisfied(context);
    }

    private Map<String, Object> getHeaders() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("ElasticsearchTargetHeader", "Output");
        headers.put("ElasticsearchQuery", "QUERY");
        headers.put("ElasticsearchDescription", "Description");
        headers.put("ElasticsearchLogIndices", 1);
        headers.put("ElasticsearchEndpoint", "mock:elasticsearch-stub");
        headers.put("ElasticsearchCacheKey", "elasticsearchCache");
        headers.put("ElasticsearchTransformer", "mock:es-transformer");
        return headers;
    }
}
