package uk.gov.companieshouse.reconciliation.service.oracle;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OracleCompanyStatusTransformerTest {

    private static final String COMPANY_STATUS_ATTRIBUTE = "CompanyStatus";
    private static final String COMPANY_STATUS_ACTIVE = "active";
    private static final String COMPANY_STATUS_DISSOLVED = "dissolved";
    private static final String COMPANY_STATUS_LIQUIDATION = "liquidation";
    private static final String INCORPORATION_NUMBER_COLUMN = "INCORPORATION_NUMBER";
    private static final String INCORPORATION_NUMBER_VALUE = "12345678";
    private static final String INCORPORATION_NUMBER_VALUE_ALT = "87654321";

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
        exchangeLiquidation.getIn().setHeader(COMPANY_STATUS_ATTRIBUTE, COMPANY_STATUS_LIQUIDATION);
        exchangeLiquidation.getIn().setBody(Collections.singletonList(new HashMap<String, Object>() {{
            put(INCORPORATION_NUMBER_COLUMN, INCORPORATION_NUMBER_VALUE);
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
                    put(INCORPORATION_NUMBER_COLUMN, INCORPORATION_NUMBER_VALUE);
                }}
        );

        // No status decorators
        List<Exchange> source = Collections.emptyList();

        Results expected = new Results(Collections.singletonList(
                new ResultModel(INCORPORATION_NUMBER_VALUE, "", COMPANY_STATUS_ACTIVE)
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
                    put(INCORPORATION_NUMBER_COLUMN, INCORPORATION_NUMBER_VALUE);
                }},
                new HashMap<String, Object>() {{
                    put(INCORPORATION_NUMBER_COLUMN, INCORPORATION_NUMBER_VALUE_ALT);
                }}
        );

        CamelContext camelContext = new DefaultCamelContext();
        // Company status decorators
        Exchange exchangeLiquidation = new DefaultExchange(camelContext);
        exchangeLiquidation.getIn().setHeader(COMPANY_STATUS_ATTRIBUTE, COMPANY_STATUS_LIQUIDATION);
        exchangeLiquidation.getIn().setBody(Collections.singletonList(new HashMap<String, Object>() {{
            put(INCORPORATION_NUMBER_COLUMN, INCORPORATION_NUMBER_VALUE);
        }}));
        Exchange exchangeDissolved = new DefaultExchange(camelContext);
        exchangeDissolved.getIn().setHeader(COMPANY_STATUS_ATTRIBUTE, COMPANY_STATUS_DISSOLVED);
        exchangeDissolved.getIn().setBody(Collections.singletonList(new HashMap<String, Object>() {{
            put(INCORPORATION_NUMBER_COLUMN, INCORPORATION_NUMBER_VALUE_ALT);
        }}));

        List<Exchange> source = Arrays.asList(exchangeLiquidation, exchangeDissolved);

        Results expected = new Results(Arrays.asList(
                new ResultModel(INCORPORATION_NUMBER_VALUE, "", COMPANY_STATUS_LIQUIDATION),
                new ResultModel(INCORPORATION_NUMBER_VALUE_ALT, "", COMPANY_STATUS_DISSOLVED))
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
        exchangeActive.getIn().setHeader(COMPANY_STATUS_ATTRIBUTE, "");
        exchangeActive.getIn().setBody(Collections.singletonList(new HashMap<String, Object>() {{
            put(INCORPORATION_NUMBER_COLUMN, INCORPORATION_NUMBER_VALUE);
        }}));
        Exchange exchangeDissolved = new DefaultExchange(camelContext);
        exchangeDissolved.getIn().setHeader(COMPANY_STATUS_ATTRIBUTE, null);
        exchangeDissolved.getIn().setBody(Collections.singletonList(new HashMap<String, Object>()));

        List<Exchange> source = Arrays.asList(exchangeActive, exchangeDissolved);

        Results expected = new Results(Collections.singletonList(
                new ResultModel(INCORPORATION_NUMBER_VALUE, "", "")
        ));

        List<Map<String, Object>> validCompanies = Collections.singletonList(
                new HashMap<String, Object>() {{
                    put(INCORPORATION_NUMBER_COLUMN, INCORPORATION_NUMBER_VALUE);
                }}
        );

        // When
        Results actual = transformer.transform(validCompanies, source);

        // Then
        assertEquals(expected, actual);
    }

}
