package uk.gov.companieshouse.reconciliation.service.mongo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;
import uk.gov.companieshouse.reconciliation.model.DisqualificationResultModel;
import uk.gov.companieshouse.reconciliation.model.DisqualificationResults;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MongoDisqualifiedOfficerResultsToResourceListTransformerTest {

    private MongoDisqualifiedOfficerResultsToResourceListTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new MongoDisqualifiedOfficerResultsToResourceListTransformer();
    }

    @Test
    void testTransform() {
        //given
        DisqualificationResults results = new DisqualificationResults(Collections.singleton(new DisqualificationResultModel("9000000000")));

        //when
        ResourceList actual = transformer.transform(results, "description");

        //then
        assertTrue(actual.contains("9000000000"));
        assertEquals("description", actual.getResultDesc());
    }
}