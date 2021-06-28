package uk.gov.companieshouse.reconciliation.service.mongo;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResultModel;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResults;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MongoInsolvencyTransformerTest {

    private MongoInsolvencyTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new MongoInsolvencyTransformer();
    }

    @Test
    void testReturnInsolvencyResultsMappedFromMongoDBResponse() {
        // given
        InsolvencyResults expected = new InsolvencyResults(Collections.singletonList(new InsolvencyResultModel("12345678", 44)));
        List<Document> response = Collections.singletonList(Document.parse("{\"_id\": \"12345678\", \"cases\": 44}"));

        // when
        InsolvencyResults actual = transformer.transform(response);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void testReturnInsolvencyResultsMappedFromMongoDBResponseNullValues() {
        // given
        InsolvencyResults expected = new InsolvencyResults(Collections.singletonList(new InsolvencyResultModel("", 0)));
        List<Document> response = Collections.singletonList(Document.parse("{\"_id\": null, \"cases\": null}"));

        // when
        InsolvencyResults actual = transformer.transform(response);

        // then
        assertEquals(expected, actual);
    }

    @Test
    void testReturnInsolvencyResultsMappedFromMongoDBResponseAbsentFields() {
        // given
        InsolvencyResults expected = new InsolvencyResults(Collections.singletonList(new InsolvencyResultModel("", 0)));
        List<Document> response = Collections.singletonList(Document.parse("{}"));

        // when
        InsolvencyResults actual = transformer.transform(response);

        // then
        assertEquals(expected, actual);
    }
}
