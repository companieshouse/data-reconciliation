package uk.gov.companieshouse.reconciliation.service.mongo;

import com.mongodb.MongoException;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Projections;
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
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class MongoAggregationRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Produce("direct:mongodb-aggregation-collection")
    private ProducerTemplate template;

    @EndpointInject("mock:myMongoEndpoint")
    private MockEndpoint mongoEndpoint;

    @EndpointInject("mock:cache")
    private MockEndpoint cache;

    @EndpointInject("mock:transformer")
    private MockEndpoint transformer;

    @AfterEach
    void setUp() {
        mongoEndpoint.reset();
        cache.reset();
        transformer.reset();
    }

    @Test
    void testRetrieveAndAggregateResultSetUncached() throws InterruptedException {
        //given
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.ACTION, CaffeineConstants.ACTION_GET, CaffeineConstants.ACTION_PUT);
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.KEY, "mongoCacheKey", "mongoCacheKey");
        cache.returnReplyHeader(CaffeineConstants.ACTION_HAS_RESULT, ExpressionBuilder.constantExpression(false));
        List<Document> mongoResponse = Arrays.asList(
                Document.parse("{\"_id\": \"12345678\", \"data\": {\"company_name\": \"ACME LTD\", \"company_status\": \"active\"}}"),
                Document.parse("{\"_id\": \"ABCD1234\", \"data\": {\"company_name\": \"DENTIST LTD\", \"company_status\": \"dissolved\"}}")
        );
        mongoEndpoint.whenAnyExchangeReceived(exchange ->
            exchange.getIn().setBody(mongoResponse)
        );
        mongoEndpoint.expectedBodyReceived().constant(Collections.singletonList(Aggregates.project(Projections.include("_id", "data.company_name", "data.company_status"))));
        Results expectedResponse = new Results(Arrays.asList(
                new ResultModel("12345678", "ACME LTD", "active"),
                new ResultModel("ABCD1234", "DENTIST LTD", "dissolved")
        ));
        transformer.expectedBodyReceived().constant(mongoResponse);
        transformer.returnReplyBody(ExpressionBuilder.constantExpression(expectedResponse));
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("MongoEndpoint", "mock:myMongoEndpoint");
        exchange.getIn().setHeader("MongoCacheKey", "mongoCacheKey");
        exchange.getIn().setHeader("MongoTransformer", "mock:transformer");
        exchange.getIn().setHeader("MongoQuery", Collections.singletonList(Aggregates.project(Projections.include("_id", "data.company_name", "data.company_status"))));

        //when
        Exchange result = template.send(exchange);
        Results actual = result.getIn().getBody(Results.class);

        //then
        assertEquals(expectedResponse, actual);
        MockEndpoint.assertIsSatisfied(camelContext);
    }

    @Test
    void testRetrieveAndAggregateResultSetCached() throws InterruptedException {
        //given
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.ACTION, CaffeineConstants.ACTION_GET);
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.KEY, "mongoCacheKey");
        cache.whenAnyExchangeReceived(exchange -> {
            exchange.getIn().setHeader(CaffeineConstants.ACTION_HAS_RESULT, true);
            exchange.getIn().setBody(new Results(Arrays.asList(new ResultModel("12345678", "ACME LTD", "active"), new ResultModel("ABCD1234", "DENTIST LTD", "dissolved"))));
        });
        mongoEndpoint.expectedMessageCount(0);
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("MongoCacheKey", "mongoCacheKey");

        //when
        Exchange result = template.send(exchange);
        Results actual = result.getIn().getBody(Results.class);

        //then
        assertTrue(actual.contains(new ResultModel("12345678", "ACME LTD", "active")));
        assertTrue(actual.contains(new ResultModel("ABCD1234", "DENTIST LTD", "dissolved")));
        MockEndpoint.assertIsSatisfied(camelContext);
    }

    @Test
    void testSetFailedHeaderIfMongoExceptionThrown() throws InterruptedException {
        //given
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.ACTION, CaffeineConstants.ACTION_GET);
        cache.expectedHeaderValuesReceivedInAnyOrder(CaffeineConstants.KEY, "mongoCompanyProfile");
        cache.returnReplyHeader(CaffeineConstants.ACTION_HAS_RESULT, ExpressionBuilder.constantExpression(false));
        mongoEndpoint.whenAnyExchangeReceived(exchange -> {
            throw new MongoException("Error");
        });
        mongoEndpoint.expectedBodyReceived().constant(Collections.singletonList(Aggregates.project(Projections.include("_id", "data.company_name", "data.company_status"))));
        Exchange exchange = new DefaultExchange(camelContext);

        //when
        Exchange result = template.send(exchange);

        //then
        assertTrue(result.getIn().getHeader("Failed", boolean.class));
        MockEndpoint.assertIsSatisfied(camelContext);
    }
}
