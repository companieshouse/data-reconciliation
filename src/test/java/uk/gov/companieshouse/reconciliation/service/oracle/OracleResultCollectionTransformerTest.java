package uk.gov.companieshouse.reconciliation.service.oracle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OracleResultCollectionTransformerTest {

    private OracleResultCollectionTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new OracleResultCollectionTransformer();
    }

    @Test
    void testFilterNullValuesFromResults() {
        //given
        List<Map<String, Object>> results = Collections.singletonList(Collections.singletonMap("RESULT", null));

        //when
        ResourceList actual = transformer.transform(results, "Description");

        //then
        assertEquals(0, actual.size());
    }

    @Test
    void testReturnStringRepresentationOfValues() {
        //given
        List<Map<String, Object>> results = Collections.singletonList(Collections.singletonMap("RESULT", 42));

        //when
        ResourceList actual = transformer.transform(results, "Description");

        //then
        assertEquals("Description", actual.getResultDesc());
        assertTrue(actual.contains("42"));
    }
}
