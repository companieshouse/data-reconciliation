package uk.gov.companieshouse.reconciliation.function.compare_results.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResultModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CompareInsolvencyCaseCountsResultMapperTest {

    private CompareInsolvencyCaseCountsResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CompareInsolvencyCaseCountsResultMapper();
    }

    @Test
    void testGenerateMappingsMapsCompanyNumbersToInsolvencyCases() {
        //given
        List<InsolvencyResultModel> models = Collections.singletonList(new InsolvencyResultModel("12345678", 3));

        //when
        Map<String, String> actual = mapper.generateMappings(models);

        //then
        assertEquals(Collections.singletonMap("12345678", "3"), actual);
    }
}