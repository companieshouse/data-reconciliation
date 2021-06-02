package uk.gov.companieshouse.reconciliation.service.mongo;

import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.model.ResultModel;
import uk.gov.companieshouse.reconciliation.model.Results;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class MongoAggregationTransformerTest {

    private MongoAggregationTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new MongoAggregationTransformer();
    }

    @Test
    void testReturnStringRepresentationOfValues() {
        //given
        List<Document> results = Collections.singletonList(Document.parse("{\"_id\": \"12345678\", \"data\": {\"company_name\": \"ACME LTD\", \"company_status\": \"active\"}}"));

        //when
        Results actual = transformer.transform(results);

        //then
        assertTrue(actual.contains(new ResultModel("12345678", "ACME LTD", "active")));
    }

    @Test
    void testDefaultEmptyStringIfIdOrCompanyNameNull(){
        //given
        List<Document> results = Collections.singletonList(Document.parse("{\"_id\": null, \"data\": {\"company_name\": null}}"));

        //when
        Results actual = transformer.transform(results);

        //then
        assertTrue(actual.contains(new ResultModel("", "")));
    }

    @Test
    void testDefaultEmptyStringIfDataNull(){
        //given
        List<Document> results = Collections.singletonList(Document.parse("{\"_id\": null, \"data\": null}}"));

        //when
        Results actual = transformer.transform(results);

        //then
        assertTrue(actual.contains(new ResultModel("", "")));
    }
}
