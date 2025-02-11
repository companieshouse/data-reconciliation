package uk.gov.companieshouse.reconciliation.function.compare_collection;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.config.aws.S3ClientConfig;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertNull;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
@Import(S3ClientConfig.class)
public class CompareCollectionRouteTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:compare_collection")
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:fruitTree")
    private MockEndpoint mockFruitTreeEndpoint;

    @EndpointInject("mock:fruitBasket")
    private MockEndpoint mockFruitBasketEndpoint;

    @EndpointInject("mock:result")
    private MockEndpoint mockCompareResult;

    @AfterEach
    void after() {
        mockFruitTreeEndpoint.reset();
        mockFruitBasketEndpoint.reset();
        mockCompareResult.reset();
    }

    @Test
    void testCompareCollections() throws InterruptedException {
        mockFruitTreeEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(new ResourceList(Arrays.asList("apple", "strawberry"), "Fruit Tree")));
        mockFruitBasketEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(new ResourceList(Arrays.asList("apple", "orange", "pineapple"), "Fruit Basket")));
        mockCompareResult.expectedBodyReceived().constant("Fruit,Exclusive To\r\nstrawberry,Fruit Tree\r\norange,Fruit Basket\r\npineapple,Fruit Basket\r\n".getBytes());
        Exchange msg = createExchange();
        producerTemplate.send(msg);
        assertNull(msg.getIn().getHeader("SrcList"));
        assertNull(msg.getIn().getHeader("TargetList"));
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void testCompareCollectionsHandleNulls() throws InterruptedException {
        mockFruitTreeEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(new ResourceList(Arrays.asList("apple", null), "Fruit Tree")));
        mockFruitBasketEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(new ResourceList(Arrays.asList("apple", "orange", "pineapple"), "Fruit Basket")));
        mockCompareResult.expectedBodyReceived().constant("Fruit,Exclusive To\r\n,Fruit Tree\r\norange,Fruit Basket\r\npineapple,Fruit Basket\r\n".getBytes());
        Exchange msg = createExchange();
        producerTemplate.send(msg);
        assertNull(msg.getIn().getHeader("SrcList"));
        assertNull(msg.getIn().getHeader("TargetList"));
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void testCompareCollectionSetsDescriptionToFailureMessageIfComparisonFailsDueToSrc() throws InterruptedException {
        mockFruitTreeEndpoint.returnReplyHeader("Failed", ExpressionBuilder.constantExpression(true));
        mockFruitBasketEndpoint.expectedMessageCount(0);
        mockCompareResult.expectedHeaderReceived("ResourceLinkDescription", "Failed to perform comparison");
        producerTemplate.send(createExchange());
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void testCompareCollectionSetsDescriptionToFailureMessageIfComparisonFailsDueToTarget() throws InterruptedException {
        mockFruitTreeEndpoint.returnReplyBody(ExpressionBuilder.constantExpression(new ResourceList(Arrays.asList("apple", null), "Fruit Tree")));
        mockFruitBasketEndpoint.returnReplyHeader("Failed", ExpressionBuilder.constantExpression(true));
        mockCompareResult.expectedHeaderReceived("ResourceLinkDescription", "Failed to perform comparison");
        producerTemplate.send(createExchange());
        MockEndpoint.assertIsSatisfied(context);
    }

    private Exchange createExchange() {
        return ExchangeBuilder.anExchange(context)
                .withHeader("Src", "mock:fruitTree")
                .withHeader("SrcDescription", "fruit tree")
                .withHeader("Target", "mock:fruitBasket")
                .withHeader("TargetDescription", "fruit basket")
                .withHeader("ComparisonDescription", "comparison")
                .withHeader("Destination", "mock:result")
                .withHeader("RecordType", "Fruit")
                .build();
    }
}
