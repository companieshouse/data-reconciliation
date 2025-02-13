package uk.gov.companieshouse.reconciliation.service.oracle;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.ExpressionBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class OracleCollectionRouteTest {

    @Autowired
    private CamelContext camelContext;

    @EndpointInject("mock:oracleEndpoint")
    private MockEndpoint oracleEndpoint;

    @EndpointInject("mock:oracleTransformer")
    private MockEndpoint oracleTransformer;

    @Produce("direct:oracle-collection")
    private ProducerTemplate template;

    @AfterEach
    void setUp() {
        this.oracleEndpoint.reset();
    }

    @Test
    void testRetrieveAndAggregateResultSet() throws InterruptedException {
        //given
        ResourceList expectedResourceList = new ResourceList(Arrays.asList("12345678", "ABCD1234"),
                "description");
        List<Map<String, Object>> expectedOracleResponse = Arrays.asList(
                Collections.singletonMap("RESULT", "12345678"),
                Collections.singletonMap("RESULT", "ABCD1234"));
        oracleEndpoint.whenAnyExchangeReceived(
                exchange -> exchange.getIn().setBody(expectedOracleResponse));
        oracleEndpoint.expectedBodiesReceived("SELECT '12345678' FROM DUAL");
        oracleTransformer.returnReplyBody(
                ExpressionBuilder.constantExpression(expectedResourceList));
        oracleTransformer.expectedBodyReceived().constant(expectedOracleResponse);
        oracleTransformer.expectedHeaderReceived("Description", "description");

        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("OracleQuery", "SELECT '12345678' FROM DUAL");
        exchange.getIn().setHeader("OracleEndpoint", "mock:oracleEndpoint");
        exchange.getIn().setHeader("OracleTransformer", "mock:oracleTransformer");
        exchange.getIn().setHeader("Description", "description");

        //when
        Exchange result = template.send(exchange);
        ResourceList resourceList = result.getIn().getBody(ResourceList.class);

        //then
        assertSame(expectedResourceList, resourceList);
        MockEndpoint.assertIsSatisfied(camelContext);
    }

    @Test
    void testSetFailedHeaderIfSQLExceptionThrown() throws InterruptedException {
        //given
        oracleEndpoint.whenAnyExchangeReceived(exchange -> {
            throw new SQLException("Failed");
        });
        oracleEndpoint.expectedBodiesReceived("SELECT '12345678' FROM DUAL");
        Exchange exchange = new DefaultExchange(camelContext);
        exchange.getIn().setHeader("OracleQuery", "SELECT '12345678' FROM DUAL");
        exchange.getIn().setHeader("OracleEndpoint", "mock:oracleEndpoint");
        exchange.getIn().setHeader("Description", "description");

        //when
        Exchange result = template.send(exchange);

        //then
        assertTrue(result.getIn().getHeader("Failed", boolean.class));
        MockEndpoint.assertIsSatisfied(camelContext);
    }
}
