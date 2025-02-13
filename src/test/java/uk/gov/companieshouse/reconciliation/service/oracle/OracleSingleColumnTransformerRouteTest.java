package uk.gov.companieshouse.reconciliation.service.oracle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.DefaultExchange;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
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
public class OracleSingleColumnTransformerRouteTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:oracle-single-column")
    private ProducerTemplate producerTemplate;

    @Test
    void testTransformInsolvencyCasesResultSet() {
        //given
        List<Map<String, Object>> expectedOracleResponse = Arrays.asList(
                new HashMap<String, Object>() {{
                    put("RESULT", "12345678");
                }}, new HashMap<String, Object>() {{
                    put("RESULT", "87654321");
                }});
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(expectedOracleResponse);
        exchange.getIn().setHeader("Description", "description");

        //when
        producerTemplate.send(exchange);
        ResourceList actual = exchange.getIn().getBody(ResourceList.class);

        //then
        assertEquals("description", actual.getResultDesc());
        assertTrue(actual.contains("12345678"));
        assertTrue(actual.contains("87654321"));
    }
}