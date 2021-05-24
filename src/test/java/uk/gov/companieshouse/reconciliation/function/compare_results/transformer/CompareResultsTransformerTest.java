package uk.gov.companieshouse.reconciliation.function.compare_results.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompareResultsTransformerTest {

    private CompareResultsTransformer transformer;

    @BeforeEach
    void setup() {
        transformer = new CompareResultsTransformer();
    }

    @Test
    void testCaptureDifferentCompanyNames() {
        // given
        Results srcResults = new Results(Arrays.asList(new ResultModel("12345678", "ACME LTD"), new ResultModel("23456789", "KICK CIC"), new ResultModel("ABCD1234", "UNLIMITED LTD")));
        Results targetResults = new Results(Arrays.asList(new ResultModel("12345678", "ACME LIMITED"), new ResultModel("23456780", "PRIVATE PLC"), new ResultModel("ABCD1234", "UNLIMITED LTD")));

        // when
        List<Map<String, Object>> actual = transformer.transform(srcResults, targetResults, "things");

        // then
        assertEquals(expectedValues(), actual);
    }

    private List<Map<String, Object>> expectedValues() {

        List<Map<String, Object>> results = new ArrayList<>();
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("Company Number", "Company Number");
        row.put("MongoDB - Company Profile", "MongoDB - Company Profile");
        row.put("Primary Search Index", "Primary Search Index");
        results.add(row);
        row = new LinkedHashMap<>();
        row.put("Company Number", "12345678");
        row.put("MongoDB - Company Profile", "ACME LTD");
        row.put("Primary Search Index", "ACME LIMITED");
        results.add(row);

        return results;
    }
}
