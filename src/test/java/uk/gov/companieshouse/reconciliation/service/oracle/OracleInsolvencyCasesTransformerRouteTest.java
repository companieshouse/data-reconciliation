package uk.gov.companieshouse.reconciliation.service.oracle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
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
import uk.gov.companieshouse.reconciliation.model.InsolvencyResultModel;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResults;

@CamelSpringBootTest
@SpringBootTest
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
public class OracleInsolvencyCasesTransformerRouteTest {

    @Autowired
    private CamelContext context;

    @Produce("direct:oracle-insolvency-cases")
    private ProducerTemplate producerTemplate;

    @Test
    void testTransformInsolvencyCasesResultSet() {
        //given
        List<Map<String, Object>> expectedOracleResponse = Arrays.asList(
                new HashMap<String, Object>() {{
                    put("INCORPORATION_NUMBER", "12345678");
                    put("NUMBER_OF_CASES", BigDecimal.valueOf(42L));
                }}, new HashMap<String, Object>() {{
                    put("INCORPORATION_NUMBER", "87654321");
                    put("NUMBER_OF_CASES", BigDecimal.valueOf(3L));
                }});
        Exchange exchange = new DefaultExchange(context);
        exchange.getIn().setBody(expectedOracleResponse);

        //when
        producerTemplate.send(exchange);

        //then
        assertEquals(new InsolvencyResults(Arrays.asList(new InsolvencyResultModel("12345678", 42),
                new InsolvencyResultModel("87654321", 3))), exchange.getIn().getBody());
    }
}