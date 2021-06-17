package uk.gov.companieshouse.reconciliation.service.mongo;

import com.mongodb.MongoException;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.component.caffeine.CaffeineConstants;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.mongodb.MongoDbConstants;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class MongoDisqualificationsCollectionRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Produce("direct:mongodb-disqualifications-collection")
    private ProducerTemplate template;

    @EndpointInject("mock:dsq_compare_target")
    private MockEndpoint mongoEndpoint;

    @EndpointInject("mock:cache")
    private MockEndpoint cache;

    @AfterEach
    void setUp() {
        mongoEndpoint.reset();
        cache.reset();
    }

    @Test
    void testRetrieveAndAggregateResultSetUncached() throws InterruptedException {
        //given
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.ACTION, CaffeineConstants.ACTION_GET, CaffeineConstants.ACTION_PUT);
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.KEY, "mongoDisqualifications", "mongoDisqualifications");
        cache.expectedBodiesReceivedInAnyOrder("undefined", Arrays.asList("12345678", "ABCD1234"));
        cache.returnReplyHeader(CaffeineConstants.ACTION_HAS_RESULT, ExpressionBuilder.constantExpression(false));
        mongoEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(Arrays.asList("12345678", "ABCD1234")));
        mongoEndpoint.expectedHeaderReceived(MongoDbConstants.DISTINCT_QUERY_FIELD, "officer_id_raw");
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setBody("undefined");

        //when
        Exchange result = template.send(exchange);
        ResourceList actual = result.getIn().getBody(ResourceList.class);

        //then
        assertTrue(actual.contains("12345678"));
        assertTrue(actual.contains("ABCD1234"));
        MockEndpoint.assertIsSatisfied(camelContext);
    }

    @Test
    void testRetrieveAndAggregateResultSetCached() throws InterruptedException {
        //given
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.ACTION, CaffeineConstants.ACTION_GET);
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.KEY, "mongoDisqualifications");
        cache.whenAnyExchangeReceived(exchange -> {
            exchange.getIn().setHeader(CaffeineConstants.ACTION_HAS_RESULT, true);
            exchange.getIn().setBody(Arrays.asList("12345678", "ABCD1234"));
        });
        mongoEndpoint.expectedMessageCount(0);
        Exchange exchange = new DefaultExchange(camelContext);

        //when
        Exchange result = template.send(exchange);
        ResourceList actual = result.getIn().getBody(ResourceList.class);

        //then
        assertTrue(actual.contains("12345678"));
        assertTrue(actual.contains("ABCD1234"));
        MockEndpoint.assertIsSatisfied(camelContext);
    }

    @Test
    void testSetFailedHeaderIfMongoExceptionThrown() throws InterruptedException {
        //given
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.ACTION, CaffeineConstants.ACTION_GET);
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.KEY, "mongoDisqualifications");
        cache.returnReplyHeader(CaffeineConstants.ACTION_HAS_RESULT, ExpressionBuilder.constantExpression(false));
        mongoEndpoint.whenAnyExchangeReceived(exchange -> {
            throw new MongoException("Error");
        });
        mongoEndpoint.expectedHeaderReceived(MongoDbConstants.DISTINCT_QUERY_FIELD, "officer_id_raw");
        Exchange exchange = new DefaultExchange(camelContext);

        //when
        Exchange result = template.send(exchange);

        //then
        assertTrue(result.getIn().getHeader("Failed", boolean.class));
        MockEndpoint.assertIsSatisfied(camelContext);
    }
}
