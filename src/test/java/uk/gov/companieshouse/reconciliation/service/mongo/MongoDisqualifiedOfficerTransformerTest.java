package uk.gov.companieshouse.reconciliation.service.mongo;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.model.DisqualificationResultModel;
import uk.gov.companieshouse.reconciliation.model.DisqualificationResults;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MongoDisqualifiedOfficerTransformerTest {

    private MongoDisqualifiedOfficerTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new MongoDisqualifiedOfficerTransformer();
    }

    @Test
    void testTransform() {
        //given
        List<Document> insolvencies = Collections.singletonList(Document.parse("{\"_id\": \"F00DFACE\", \"officer_id_raw\": \"9000000000\"}"));

        //when
        DisqualificationResults actual = transformer.transform(insolvencies);

        //then
        assertTrue(actual.contains(new DisqualificationResultModel("9000000000")));
    }

    @Test
    void testTransformNullOfficerIdRawField() {
        //given
        List<Document> insolvencies = Collections.singletonList(Document.parse("{\"_id\": \"F00DFACE\", \"officer_id_raw\": null}"));

        //when
        DisqualificationResults actual = transformer.transform(insolvencies);

        //then
        assertTrue(actual.contains(new DisqualificationResultModel("")));
    }

    @Test
    void testTransformAbsentOfficerIdRawField() {
        //given
        List<Document> insolvencies = Collections.singletonList(Document.parse("{\"_id\": \"F00DFACE\"}"));

        //when
        DisqualificationResults actual = transformer.transform(insolvencies);

        //then
        assertTrue(actual.contains(new DisqualificationResultModel("")));
    }

    @Test
    void testSkipNullDocuments() {
        //given
        List<Document> insolvencies = Collections.singletonList(null);

        //when
        DisqualificationResults actual = transformer.transform(insolvencies);

        //then
        assertEquals(0, actual.size());
    }
}