package uk.gov.companieshouse.reconciliation.function.compare_results.transformer;

import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompareFieldsResultsTransformerTest {

    private CompareFieldsResultsTransformer transformer;

    @BeforeEach
    void setup() {
        transformer = new CompareFieldsResultsTransformer();
    }

    @Test
    void testCaptureDifferentCompanyNames() {
        // given
        Results srcResults = new Results(Arrays.asList(new ResultModel("12345678", "ACME LTD"),
                new ResultModel("23456789", "KICK CIC"),
                new ResultModel("ABCD1234", "UNLIMITED LTD")));
        Results targetResults = new Results(
                Arrays.asList(new ResultModel("12345678", "ACME LIMITED"),
                        new ResultModel("23456780", "PRIVATE PLC"),
                        new ResultModel("ABCD1234", "UNLIMITED LTD")));

        // when
        List<Map<String, Object>> actual = transformer
                .transform(srcResults, "MongoDB - Company Profile", targetResults,
                        "Primary Search Index", "Company Number", (a) -> a.stream().collect(
                                Collectors.toMap(ResultModel::getCompanyNumber,
                                        ResultModel::getCompanyName)));

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
