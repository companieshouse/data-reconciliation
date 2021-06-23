package uk.gov.companieshouse.reconciliation.service.oracle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResultModel;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResults;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OracleInsolvencyCasesTransformerTest {

    private OracleInsolvencyCasesTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new OracleInsolvencyCasesTransformer();
    }

    @Test
    void testMapResultSetToInsolvencyResultsEntity() {
        // given
        List<Map<String, Object>> resultSet = Collections.singletonList(new LinkedHashMap<String, Object>(){{
            put("INCORPORATION_NUMBER", "12345678");
            put("NUMBER_OF_CASES", BigDecimal.valueOf(3L));
        }});

        // when
        InsolvencyResults actual = transformer.transform(resultSet);

        // then
        assertEquals(new InsolvencyResults(Collections.singletonList(new InsolvencyResultModel("12345678", 3))), actual);
    }

    @Test
    void testUseZeroValuesIfNull() {
        // when
        InsolvencyResults actual = transformer.transform(Collections.singletonList(Collections.emptyMap()));

        // then
        assertEquals(new InsolvencyResults(Collections.singletonList(new InsolvencyResultModel("", 0))), actual);
    }

    @Test
    void testSkipNullRows() {
        // given
        List<Map<String, Object>> resultSet = Arrays.asList(new LinkedHashMap<String, Object>() {{
            put("INCORPORATION_NUMBER", "12345678");
            put("NUMBER_OF_CASES", BigDecimal.valueOf(3L));
        }}, null);

        // when
        InsolvencyResults actual = transformer.transform(resultSet);

        // then
        assertEquals(new InsolvencyResults(Collections.singletonList(new InsolvencyResultModel("12345678", 3))), actual);
    }
}