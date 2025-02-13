package uk.gov.companieshouse.reconciliation.service.mongo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;
import uk.gov.companieshouse.reconciliation.model.DisqualificationResultModel;
import uk.gov.companieshouse.reconciliation.model.DisqualificationResults;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class MongoDisqualifiedOfficerMapperTest {

    @Autowired
    private CamelContext context;

    @Autowired
    private List<Bson> disqualifiedOfficerAggregationQuery;

    @EndpointInject("mock:mongoAggregation")
    private MockEndpoint companyProfileCollection;

    @Produce("direct:mongo-disqualified_officer-mapper")
    private ProducerTemplate producerTemplate;

    @AfterEach
    void tearDown() {
        companyProfileCollection.reset();
    }

    @Test
    void testFetchAndTransformDisqualifiedOfficers() throws InterruptedException {
        companyProfileCollection.expectedHeaderReceived("MongoEndpoint", "mock:dsq_compare_target");
        companyProfileCollection.expectedHeaderReceived("MongoCacheKey", "mongoDisqualifications");
        companyProfileCollection.expectedHeaderReceived("MongoTransformer",
                "direct:disqualified-officer-transformer");
        companyProfileCollection.expectedHeaderReceived("MongoQuery",
                disqualifiedOfficerAggregationQuery);
        companyProfileCollection.whenAnyExchangeReceived(exchange -> {
            exchange.getIn().setBody(new DisqualificationResults(
                    Collections.singleton(new DisqualificationResultModel("9000000000"))));
        });
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("Description", "description");
        Exchange actual = producerTemplate.send(exchange);
        ResourceList resources = actual.getIn().getBody(ResourceList.class);

        assertTrue(resources.contains("9000000000"));
        assertFalse(actual.getIn().getHeader("Failed", boolean.class));
        MockEndpoint.assertIsSatisfied(context);
    }

    @Test
    void testSkipTransformIfFailed() throws InterruptedException {
        companyProfileCollection.expectedHeaderReceived("MongoEndpoint", "mock:dsq_compare_target");
        companyProfileCollection.expectedHeaderReceived("MongoCacheKey", "mongoDisqualifications");
        companyProfileCollection.expectedHeaderReceived("MongoTransformer",
                "direct:disqualified-officer-transformer");
        companyProfileCollection.expectedHeaderReceived("MongoQuery",
                disqualifiedOfficerAggregationQuery);
        companyProfileCollection.returnReplyHeader("Failed",
                ExpressionBuilder.constantExpression(true));
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeader("Description", "description");
        Exchange actual = producerTemplate.send(exchange);

        assertTrue(actual.getIn().getHeader("Failed", boolean.class));
        MockEndpoint.assertIsSatisfied(context);
    }
}
