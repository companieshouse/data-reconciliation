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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class OracleMultiCollectionRouteTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:oracle-multi-collection")
    private ProducerTemplate producerTemplate;

    @EndpointInject("mock:oracleEndpoint")
    private MockEndpoint oracleEndpoint;

    @AfterEach
    void tearDown() {
        oracleEndpoint.reset();
    }

    @Test
    void testOneOracleRequestPerSqlQuery() throws InterruptedException {
        oracleEndpoint.expectedBodiesReceivedInAnyOrder("SELECT '12345678' as incorporation_number, 'active' as company_status FROM DUAL", "SELECT '87654321' as incorporation_number, 'dissolved' as company_status FROM DUAL");
        oracleEndpoint.whenExchangeReceived(1, exchange -> {
            exchange.getIn().setBody(Collections.singletonList(new HashMap<String, Object>(){{
                put("incorporation_number", "12345678");
                put("company_status", "active");
            }}));
        });
        oracleEndpoint.whenExchangeReceived(2, exchange -> {
            exchange.getIn().setBody(Collections.singletonList(new HashMap<String, Object>(){{
                put("incorporation_number", "87654321");
                put("company_status", "dissolved");
            }}));
        });
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setHeaders(headers());
        producerTemplate.send(exchange);
        Results actual = exchange.getIn().getBody(Results.class);
        assertEquals(new Results(Arrays.asList(new ResultModel("12345678", "", "active"), new ResultModel("87654321", "", "dissolved"))), actual);
        MockEndpoint.assertIsSatisfied(context);
    }

    private Map<String, Object> headers() {
        Map<String, Object> result = new HashMap<>();
        result.put("OracleQuery", "<sql-statements><sql-statement>SELECT '12345678' as incorporation_number, 'active' as company_status FROM DUAL</sql-statement><sql-statement>SELECT '87654321' as incorporation_number, 'dissolved' as company_status FROM DUAL</sql-statement></sql-statements>");
        result.put("OracleEndpoint", "mock:oracleEndpoint");
        return result;
    }
}
