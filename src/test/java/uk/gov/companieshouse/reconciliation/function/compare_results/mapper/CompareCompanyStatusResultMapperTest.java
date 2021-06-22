package uk.gov.companieshouse.reconciliation.function.compare_results.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.model.ResultModel;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompareCompanyStatusResultMapperTest {

    private CompanyResultsMappable mapper;

    @BeforeEach
    void setUp() {
        mapper = new CompareCompanyStatusResultMapper();
    }

    @Test
    void testGenerateMappingsCorrectlyMapsResultsModelsToCompanyStatus() {
        // given
        Collection<ResultModel> resultModels = Collections.singletonList(new ResultModel("1234578", "", "active"));

        // when
        Map<String, String> actual = mapper.generateMappings(resultModels);

        // then
        assertEquals(Collections.singletonMap("1234578", "active"), actual);
    }

    @Test
    void testGenerateMappingsWhenCompanyStatusIsNull() {
        // given
        Collection<ResultModel> resultModels = Collections.singletonList(new ResultModel("1234578", "", null));

        // when
        Map<String, String> actual = mapper.generateMappings(resultModels);

        // then
        assertEquals(Collections.singletonMap("1234578", ""), actual);
    }

    @Test
    void testGenerateMappingsWhenCompanyStatusIsEmpty() {
        // given
        Collection<ResultModel> resultModels = Collections.singletonList(new ResultModel("1234578", "", ""));

        // when
        Map<String, String> actual = mapper.generateMappings(resultModels);

        // then
        assertEquals(Collections.singletonMap("1234578", ""), actual);
    }
}
