package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class MongoCollectionRouteTest {

    @Autowired
    private CamelContext camelContext;

    @Produce("direct:mongodb-collection")
    private ProducerTemplate template;

    @EndpointInject("mock:mongoEndpoint")
    private MockEndpoint mongoEndpoint;

    @AfterEach
    void setUp() {
        this.mongoEndpoint.reset();
    }

    @Test
    void testRetrieveAndAggregateResultSet() throws InterruptedException {
        //given
        mongoEndpoint.whenAnyExchangeReceived(exchange ->
            exchange.getIn().setBody(Arrays.asList(
                    "12345678",
                    "ABCD1234"
            ))
        );
        mongoEndpoint.expectedHeaderReceived(MongoDbConstants.DISTINCT_QUERY_FIELD, "_id");
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader(MongoDbConstants.DISTINCT_QUERY_FIELD, "_id");
        exchange.getIn().setHeader("MongoEndpoint", "mock:mongoEndpoint");
        exchange.getIn().setHeader("MongoDescription", "description");
        exchange.getIn().setHeader("MongoTargetHeader", "target");

        //when
        Exchange result = template.send(exchange);
        ResourceList resourceList = result.getIn().getHeader("target", ResourceList.class);

        //then
        assertEquals("description", resourceList.getResultDesc());
        assertTrue(resourceList.getResultList().contains("12345678"));
        assertTrue(resourceList.getResultList().contains("ABCD1234"));
        MockEndpoint.assertIsSatisfied(camelContext);
    }
}
