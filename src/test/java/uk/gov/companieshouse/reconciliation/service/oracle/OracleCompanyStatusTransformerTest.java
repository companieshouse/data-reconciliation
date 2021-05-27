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
                    put("incorporation_number", "12345678");
                    put("company_status", "active");
                }}),
                Collections.singletonList(new HashMap<String, Object>() {{
                    put("incorporation_number", "87654321");
                    put("company_status", "dissolved");
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
                    put("incorporation_number", null);
                    put("company_status", null);
                }}),
                Collections.singletonList(new HashMap<String, Object>() {{
                    put("incorporation_number", null);
                    put("company_status", null);
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
