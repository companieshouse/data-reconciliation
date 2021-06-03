package uk.gov.companieshouse.reconciliation.function.compare_results.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.model.ResultModel;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompareCompanyNameResultMapperTest {

    private CompanyResultsMappable mapper;

    @BeforeEach
    void setUp() {
        mapper = new CompareCompanyNameResultMapper();
    }

    @Test
    void testGenerateMappingsCorrectlyMapsResultsModelsToCompanyName() {
        // given
        Collection<ResultModel> resultModels = Collections.singletonList(new ResultModel("1234578", "ACME Limited"));

        // when
        Map<String, String> actual = mapper.generateMappings(resultModels);

        // then
        assertEquals(new HashMap<String, String>() {{
                put("1234578", "ACME Limited");
            }}, actual);
    }

    @Test
    void testGenerateMappingsWhenCompanyNameIsNull() {
        // given
        Collection<ResultModel> resultModels = Collections.singletonList(new ResultModel("1234578", null));

        // when
        Map<String, String> actual = mapper.generateMappings(resultModels);

        // then
        assertEquals(new HashMap<String, String>() {{
            put("1234578", "");
        }}, actual);
    }

    @Test
    void testGenerateMappingsWhenCompanyIsEmpty() {
        // given
        Collection<ResultModel> resultModels = Collections.singletonList(new ResultModel("1234578", ""));

        // when
        Map<String, String> actual = mapper.generateMappings(resultModels);

        // then
        assertEquals(new HashMap<String, String>() {{
            put("1234578", "");
        }}, actual);
    }
}
