package uk.gov.companieshouse.reconciliation.service.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
        Map<String, Object> headers = new HashMap<>();
        Results resultModels = new Results(Collections.singletonList(new ResultModel("12345678", "ACME LIMITED")));

        //when
        transformer.transform(resultModels, "Description", "Target", headers);
        ResourceList actual = (ResourceList)headers.get("Target");

        //then
        assertTrue(actual.contains("12345678"));
        assertEquals("Description", actual.getResultDesc());
    }
}
