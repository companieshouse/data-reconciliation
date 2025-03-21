package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.config.aws.S3ClientConfig;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResultModel;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResults;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
@Import(S3ClientConfig.class)
public class MongoInsolvencyMapperTest {

    @Autowired
    private CamelContext context;

    @Autowired
    private List<Bson> insolvencyAggregationQuery;

    @EndpointInject("mock:mongoAggregation")
    private MockEndpoint companyProfileCollection;

    @Produce("direct:mongo-insolvency-mapper")
    private ProducerTemplate producerTemplate;

    @AfterEach
    void tearDown() {
        companyProfileCollection.reset();
    }

    @Test
    void testFetchAndTransformInsolvencyCases() throws InterruptedException {
        companyProfileCollection.expectedHeaderReceived("MongoEndpoint", "mock:insolvency_cases");
        companyProfileCollection.expectedHeaderReceived("MongoCacheKey", "mongoInsolvencies");
        companyProfileCollection.expectedHeaderReceived("MongoTransformer", "direct:mongo-insolvency_cases-transformer");
        companyProfileCollection.expectedHeaderReceived("MongoQuery", insolvencyAggregationQuery);
        companyProfileCollection.whenAnyExchangeReceived(exchange -> {
            exchange.getIn().setBody(new InsolvencyResults(Collections.singleton(new InsolvencyResultModel("12345678", 42))));
        });
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("Description", "description");
        Exchange actual = producerTemplate.send(exchange);
        ResourceList resources = actual.getIn().getBody(ResourceList.class);

        assertTrue(resources.contains("12345678"));
        assertEquals("description", resources.getResultDesc());
        assertFalse(actual.getIn().getHeader("Failed", boolean.class));
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void testSkipTransformIfFailed() throws InterruptedException {
        companyProfileCollection.expectedHeaderReceived("MongoEndpoint", "mock:insolvency_cases");
        companyProfileCollection.expectedHeaderReceived("MongoCacheKey", "mongoInsolvencies");
        companyProfileCollection.expectedHeaderReceived("MongoTransformer", "direct:mongo-insolvency_cases-transformer");
        companyProfileCollection.expectedHeaderReceived("MongoQuery", insolvencyAggregationQuery);
        companyProfileCollection.returnReplyHeader("Failed", ExpressionBuilder.constantExpression(true));
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("Description", "description");
        Exchange actual = producerTemplate.send(exchange);

        assertTrue(actual.getIn().getHeader("Failed", boolean.class));
        MockEndpoint.assertIsSatisfied(context);
    }
}
