package uk.gov.companieshouse.reconciliation.function.compare_collection;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class CompareListRouteTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:compare_collection")
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:fruitTree")
    private MockEndpoint mockFruitTreeEndpoint;

    @EndpointInject("mock:fruitBasket")
    private MockEndpoint mockFruitBasketEndpoint;

    @EndpointInject("mock:compare_result")
    private MockEndpoint mockCompareResult;

    @AfterEach
    void after() {
        mockFruitTreeEndpoint.reset();
        mockFruitBasketEndpoint.reset();
        mockCompareResult.reset();
    }

    @Test
    void testCompareCollections() throws InterruptedException {
        mockFruitTreeEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(Arrays.asList(Collections.singletonMap("RESULT", "apple"), Collections.singletonMap("RESULT", "strawberry"))));
        mockFruitBasketEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(Arrays.asList("apple", "orange", "pineapple")));
        mockCompareResult.allMessages().body().isEqualTo("item,source\r\nstrawberry,Fruit Tree\r\norange,Fruit Basket\r\npineapple,Fruit Basket\r\n".getBytes());
        producerTemplate.sendBodyAndHeaders(0, createHeaders());
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void testCompareCollectionsHandleNulls() throws InterruptedException {
        mockFruitTreeEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(Arrays.asList(Collections.singletonMap("RESULT", "apple"), Collections.singletonMap("RESULT", null))));
        mockFruitBasketEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(Arrays.asList("apple", "orange", "pineapple")));
        mockCompareResult.allMessages().body().isEqualTo("item,source\r\n,Fruit Tree\r\norange,Fruit Basket\r\npineapple,Fruit Basket\r\n".getBytes());
        producerTemplate.sendBodyAndHeaders(0, createHeaders());
        MockEndpoint.assertIsSatisfied(context);
    }

    private Map<String, Object> createHeaders() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("Src", "mock:fruitTree");
        headers.put("SrcName", "Fruit Tree");
        headers.put("Target", "mock:fruitBasket");
        headers.put("TargetName", "Fruit Basket");
        headers.put("Comparison", "fruit");
        headers.put("Destination", "mock:compare_result");
        return headers;
    }
}
