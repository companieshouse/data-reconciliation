package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class OracleCollectionRouteTest {

    @Autowired
    private CamelContext camelContext;

    @EndpointInject("mock:oracleEndpoint")
    private MockEndpoint oracleEndpoint;

    @Produce("direct:oracle-collection")
    private ProducerTemplate template;

    @AfterEach
    void setUp() {
        this.oracleEndpoint.reset();
    }

    @Test
    void testRetrieveAndAggregateResultSet() throws InterruptedException {
        //given
        oracleEndpoint.whenAnyExchangeReceived(exchange ->
            exchange.getIn().setBody(Arrays.asList(
                    Collections.singletonMap("RESULT", "12345678"),
                    Collections.singletonMap("RESULT", "ABCD1234")
            ))
        );
        oracleEndpoint.expectedBodiesReceived("SELECT '12345678' FROM DUAL");
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("OracleQuery", "SELECT '12345678' FROM DUAL");
        exchange.getIn().setHeader("OracleEndpoint", "mock:oracleEndpoint");
        exchange.getIn().setHeader("OracleTargetHeader", "target");
        exchange.getIn().setHeader("OracleDescription", "description");

        //when
        Exchange result = template.send(exchange);
        ResourceList resourceList = result.getIn().getHeader("target", ResourceList.class);

        //then
        assertEquals("description", resourceList.getResultDesc());
        assertEquals(Arrays.asList("12345678", "ABCD1234"), resourceList.getResultList());
        MockEndpoint.assertIsSatisfied(camelContext);
    }
}