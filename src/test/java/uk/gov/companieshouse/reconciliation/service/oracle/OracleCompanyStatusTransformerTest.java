package uk.gov.companieshouse.reconciliation.service.oracle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

public class OracleCompanyStatusTransformerTest {


    @Test
    void testTransformResultSetIntoResultsObject() {
        // Given
        List<List<Map<String, Object>>> source = Arrays.asList(
                Collections.singletonList(new HashMap<String, Object>(){{
                    put("incorporation_number", "12345678");
                    put("company_status", "active");
                }}),
                Collections.singletonList(new HashMap<String, Object>(){{
                    put("incorporation_number", "87654321");
                    put("company_status", "dissolved");
                }}));
        Results target = new Results(Arrays.asList(new ResultModel("12345678", "", "active"),
                new ResultModel("87654321", "", "dissolved")));

        // When
        OracleCompanyStatusTransformer transformer = new OracleCompanyStatusTransformer();
        Results actual = transformer.transform(source);

        // Then
        assertEquals(target, actual);
    }

}
