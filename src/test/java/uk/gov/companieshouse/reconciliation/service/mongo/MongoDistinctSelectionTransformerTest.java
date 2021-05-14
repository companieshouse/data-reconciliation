package uk.gov.companieshouse.reconciliation.service.mongo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MongoDistinctSelectionTransformerTest {

    private MongoDistinctSelectionTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new MongoDistinctSelectionTransformer();
    }

    @Test
    void testFilterNullValuesFromResult() {
        //given
        List<Object> results = Collections.singletonList(null);
        Map<String, Object> headers = new HashMap<>();

        //when
        transformer.transform(results, "Description", "Target", headers);
        ResourceList actual = (ResourceList) headers.get("Target");

        //then
        assertEquals(0, actual.size());
    }

    @Test
    void testReturnStringRepresentationOfValues() {
        //given
        List<Object> results = Collections.singletonList(42);
        Map<String, Object> headers = new HashMap<>();

        //when
        transformer.transform(results, "Description", "Target", headers);
        ResourceList actual = (ResourceList) headers.get("Target");

        //then
        assertTrue(headers.containsKey("Target"));
        assertEquals("Description", actual.getResultDesc());
        assertTrue(actual.contains("42"));
    }
}
