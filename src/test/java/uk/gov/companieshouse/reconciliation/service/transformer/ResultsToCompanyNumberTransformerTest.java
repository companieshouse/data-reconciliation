package uk.gov.companieshouse.reconciliation.service.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultsToCompanyNumberTransformerTest {

    private ResultsToCompanyNumberTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new ResultsToCompanyNumberTransformer();
    }

    @Test
    void testTransformResultModelsIntoResourceList() {
        //given
        Results resultModels = new Results(Collections.singletonList(new ResultModel("12345678", "ACME LIMITED")));

        //when
        ResourceList actual = transformer.transform(resultModels, "Description");

        //then
        assertTrue(actual.contains("12345678"));
        assertEquals("Description", actual.getResultDesc());
    }
}
