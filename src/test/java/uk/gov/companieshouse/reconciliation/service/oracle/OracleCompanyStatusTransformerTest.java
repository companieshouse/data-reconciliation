package uk.gov.companieshouse.reconciliation.service.oracle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

public class OracleCompanyStatusTransformerTest {

    private OracleCompanyStatusTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new OracleCompanyStatusTransformer();
    }

    @Test
    void testTransformResultSetIntoResultsObject() {
        final String companyStatus = "active";

        // Given
        CamelContext camelContext = new DefaultCamelContext();
        Exchange exchangeActive = new DefaultExchange(camelContext);
        exchangeActive.getIn().setHeader("CompanyStatus", "active");
        exchangeActive.getIn().setBody(Collections.singletonList(new HashMap<String, Object>() {{
                    put("INCORPORATION_NUMBER", "12345678");
                }}));
        Exchange exchangeDissolved = new DefaultExchange(camelContext);
        exchangeDissolved.getIn().setHeader("CompanyStatus", "dissolved");
        exchangeDissolved.getIn().setBody(Collections.singletonList(new HashMap<String, Object>() {{
                    put("INCORPORATION_NUMBER", "87654321");
                }}));

        List<Exchange> source = Arrays.asList(exchangeActive, exchangeDissolved);

        Results expected = new Results(Arrays.asList(
                new ResultModel("12345678", "", "active"),
                new ResultModel("87654321", "", "dissolved"))
        );

        // When
        Results actual = transformer.transform(source);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testTransformResultSetWithNullValuesIntoResultsObject() {
        // Given
        CamelContext camelContext = new DefaultCamelContext();
        Exchange exchangeActive = new DefaultExchange(camelContext);
        exchangeActive.getIn().setHeader("CompanyStatus", "");
        exchangeActive.getIn().setBody(Collections.singletonList(new HashMap<String, Object>() {{
            put("INCORPORATION_NUMBER", null);
        }}));
        Exchange exchangeDissolved = new DefaultExchange(camelContext);
        exchangeDissolved.getIn().setHeader("CompanyStatus", null);
        exchangeDissolved.getIn().setBody(Collections.singletonList(new HashMap<String, Object>()));

        List<Exchange> source = Arrays.asList(exchangeActive, exchangeDissolved);

        Results expected = new Results(Arrays.asList(
                new ResultModel("", "", ""),
                new ResultModel("", "", ""))
        );

        // When
        Results actual = transformer.transform(source);

        // Then
        assertEquals(expected, actual);
    }

}
