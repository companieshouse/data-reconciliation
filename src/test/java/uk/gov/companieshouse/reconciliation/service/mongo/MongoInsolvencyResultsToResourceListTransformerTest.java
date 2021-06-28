package uk.gov.companieshouse.reconciliation.service.mongo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResultModel;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResults;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MongoInsolvencyResultsToResourceListTransformerTest {

    private MongoInsolvencyResultsToResourceListTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new MongoInsolvencyResultsToResourceListTransformer();
    }

    @Test
    void testTransform() {
        //given
        InsolvencyResults results = new InsolvencyResults(Arrays.asList(
                new InsolvencyResultModel("12345678", 3),
                new InsolvencyResultModel("87654321", 42)));

        //when
        ResourceList actual = transformer.transform(results, "description");

        //then
        assertTrue(actual.contains("12345678"));
        assertEquals(2, actual.size());
        assertEquals("description", actual.getResultDesc());
    }
}