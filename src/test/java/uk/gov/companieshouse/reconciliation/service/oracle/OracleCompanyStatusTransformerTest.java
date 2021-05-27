package uk.gov.companieshouse.reconciliation.service.oracle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        // Given
        List<List<Map<String, Object>>> source = Arrays.asList(
                Collections.singletonList(new HashMap<String, Object>() {{
                    put("INCORPORATION_NUMBER", "12345678");
                    put("COMPANY_STATUS", "active");
                }}),
                Collections.singletonList(new HashMap<String, Object>() {{
                    put("INCORPORATION_NUMBER", "87654321");
                    put("COMPANY_STATUS", "dissolved");
                }}));
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
        List<List<Map<String, Object>>> source = Arrays.asList(
                Collections.singletonList(new HashMap<String, Object>() {{
                    put("INCORPORATION_NUMBER", null);
                    put("COMPANY_STATUS", null);
                }}),
                Collections.singletonList(new HashMap<String, Object>() {{
                    put("INCORPORATION_NUMBER", null);
                    put("COMPANY_STATUS", null);
                }}),
                Collections.singletonList(new HashMap<>()));
        Results expected = new Results(Arrays.asList(
                new ResultModel("", "", ""),
                new ResultModel("", "", ""),
                new ResultModel("", "", ""))
        );

        // When
        Results actual = transformer.transform(source);

        // Then
        assertEquals(expected, actual);
    }

}
