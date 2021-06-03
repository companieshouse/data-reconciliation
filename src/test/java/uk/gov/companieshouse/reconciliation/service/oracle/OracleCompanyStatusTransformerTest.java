package uk.gov.companieshouse.reconciliation.service.oracle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
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
    void testTransformResultSetIntoResultsObjectWhenNoValidCompanies() {
        // Given

        // No valid companies
        List<Map<String, Object>> validCompanies = Collections.emptyList();

        CamelContext camelContext = new DefaultCamelContext();

        // Status decorator - will be ignored
        Exchange exchangeLiquidation = new DefaultExchange(camelContext);
        exchangeLiquidation.getIn().setHeader("CompanyStatus", "liquidation");
        exchangeLiquidation.getIn().setBody(Collections.singletonList(new HashMap<String, Object>() {{
            put("INCORPORATION_NUMBER", "12345678");
        }}));
        List<Exchange> source = Collections.emptyList();

        Results expected = new Results(Collections.emptyList());

        // When
        Results actual = transformer.transform(validCompanies, source);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testTransformResultSetIntoResultsObjectWhenCompanyStatusIsActive() {
        // Given
        List<Map<String, Object>> validCompanies = Collections.singletonList(
                new HashMap<String, Object>() {{
                    put("INCORPORATION_NUMBER", "12345678");
                }}
        );

        // No status decorators
        List<Exchange> source = Collections.emptyList();

        Results expected = new Results(Collections.singletonList(
                new ResultModel("12345678", "", "active")
        ));

        // When
        Results actual = transformer.transform(validCompanies, source);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testTransformResultSetIntoResultsObjectWhenDecoratingCompanyStatus() {
        // Given
        List<Map<String, Object>> validCompanies = Arrays.asList(
                new HashMap<String, Object>() {{
                    put("INCORPORATION_NUMBER", "12345678");
                }},
                new HashMap<String, Object>() {{
                    put("INCORPORATION_NUMBER", "87654321");
                }}
        );

        CamelContext camelContext = new DefaultCamelContext();
        // Company status decorators
        Exchange exchangeLiquidation = new DefaultExchange(camelContext);
        exchangeLiquidation.getIn().setHeader("CompanyStatus", "liquidation");
        exchangeLiquidation.getIn().setBody(Collections.singletonList(new HashMap<String, Object>() {{
            put("INCORPORATION_NUMBER", "12345678");
        }}));
        Exchange exchangeDissolved = new DefaultExchange(camelContext);
        exchangeDissolved.getIn().setHeader("CompanyStatus", "dissolved");
        exchangeDissolved.getIn().setBody(Collections.singletonList(new HashMap<String, Object>() {{
            put("INCORPORATION_NUMBER", "87654321");
        }}));

        List<Exchange> source = Arrays.asList(exchangeLiquidation, exchangeDissolved);

        Results expected = new Results(Arrays.asList(
                new ResultModel("12345678", "", "liquidation"),
                new ResultModel("87654321", "", "dissolved"))
        );

        // When
        Results actual = transformer.transform(validCompanies, source);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void testTransformResultSetWithNullCompanyStatusIntoResultsObject() {
        // Given
        CamelContext camelContext = new DefaultCamelContext();
        Exchange exchangeActive = new DefaultExchange(camelContext);
        exchangeActive.getIn().setHeader("CompanyStatus", "");
        exchangeActive.getIn().setBody(Collections.singletonList(new HashMap<String, Object>() {{
            put("INCORPORATION_NUMBER", "12345678");
        }}));
        Exchange exchangeDissolved = new DefaultExchange(camelContext);
        exchangeDissolved.getIn().setHeader("CompanyStatus", null);
        exchangeDissolved.getIn().setBody(Collections.singletonList(new HashMap<String, Object>()));

        List<Exchange> source = Arrays.asList(exchangeActive, exchangeDissolved);

        Results expected = new Results(Collections.singletonList(
                new ResultModel("12345678", "", "")
        ));

        List<Map<String, Object>> validCompanies = Collections.singletonList(
                new HashMap<String, Object>() {{
                    put("INCORPORATION_NUMBER", "12345678");
                }}
        );

        // When
        Results actual = transformer.transform(validCompanies, source);

        // Then
        assertEquals(expected, actual);
    }

}
