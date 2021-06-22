package uk.gov.companieshouse.reconciliation.service.mongo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MongoDistinctToResourceListTransformerTest {

    private MongoDistinctToResourceListTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new MongoDistinctToResourceListTransformer();
    }

    @Test
    void transformDistinctSelectionIntoResourceList() {
        //given
        List<String> results = Arrays.asList("12345678", null, "ABCD1234");

        //when
        ResourceList actual = transformer.transform(results, "description");

        //then
        assertTrue(actual.contains("12345678"));
        assertTrue(actual.contains("ABCD1234"));
        assertEquals(2, actual.size());
    }
}
