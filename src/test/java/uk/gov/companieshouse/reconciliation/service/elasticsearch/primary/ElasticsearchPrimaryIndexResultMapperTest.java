package uk.gov.companieshouse.reconciliation.service.elasticsearch.primary;

import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.model.ResultModel;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ElasticsearchPrimaryIndexResultMapperTest {

    private ElasticsearchPrimaryIndexResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ElasticsearchPrimaryIndexResultMapper();
    }

    @Test
    void testMapSearchHitIntoResultModel() {
        //given
        String source = "{ \"items\": [{\"corporate_name_start\": \"ACME\", \"corporate_name_ending\": \" LIMITED\"}] }";
        SearchHit hit = new SearchHit(123, "12345678", new Text("{}"), Collections.emptyMap());
        hit.sourceRef(new BytesArray(source));

        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);

        //then
        assertEquals(new ResultModel("12345678", "ACME LIMITED"), actual);
    }

    @Test
    void testMapSearchHitWithoutSourceFields() {
        //given
        SearchHit hit = new SearchHit(123, "12345678", new Text("{}"), Collections.emptyMap());

        //when
        ResultModel actual = mapper.mapExcludingSourceFields(hit);

        //then
        assertEquals(new ResultModel("12345678", ""), actual);
    }

    @Test
    void testMapSearchHitReplaceNullValuesWithEmptyStrings() {
        //given
        String source = "{ \"items\": [{\"corporate_name_start\": null, \"corporate_name_ending\": null}] }";
        SearchHit hit = new SearchHit(123, "12345678", new Text("{}"), Collections.emptyMap());
        hit.sourceRef(new BytesArray(source));

        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);

        //then
        assertEquals(new ResultModel("12345678", ""), actual);
    }

    @Test
    void testMapSearchHitHandleEmptyItemsArray() {
        //given
        String source = "{ \"items\": [] }";
        SearchHit hit = new SearchHit(123, "12345678", new Text("{}"), Collections.emptyMap());
        hit.sourceRef(new BytesArray(source));

        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);

        //then
        assertEquals(new ResultModel("12345678", ""), actual);
    }

    @Test
    void testMapSearchHitHandleNullItems() {
        //given
        String source = "{ \"items\": null }";
        SearchHit hit = new SearchHit(123, "12345678", new Text("{}"), Collections.emptyMap());
        hit.sourceRef(new BytesArray(source));

        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);

        //then
        assertEquals(new ResultModel("12345678", ""), actual);
    }

    @Test
    void testMapSearchHitNoSpaceBetweenNameStartAndNameEnding() {
        //given
        String source = "{ \"items\": [{\"corporate_name_start\": \"ACME\", \"corporate_name_ending\": \"LIMITED\"}] }";
        SearchHit hit = new SearchHit(123, "12345678", new Text("{}"), Collections.emptyMap());
        hit.sourceRef(new BytesArray(source));

        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);

        //then
        assertEquals(new ResultModel("12345678", "ACME LIMITED"), actual);
    }

    @Test
    void testMapSearchHitNameEndingAbsent() {
        //given
        String source = "{ \"items\": [{\"corporate_name_start\": \"ACME\", \"corporate_name_ending\": \"\"}] }";
        SearchHit hit = new SearchHit(123, "12345678", new Text("{}"), Collections.emptyMap());
        hit.sourceRef(new BytesArray(source));

        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);

        //then
        assertEquals(new ResultModel("12345678", "ACME"), actual);
    }

    @Test
    void testMapSearchHitNameStartAbsent() {
        //given
        String source = "{ \"items\": [{\"corporate_name_start\": \"\", \"corporate_name_ending\": \" LIMITED\"}] }";
        SearchHit hit = new SearchHit(123, "12345678", new Text("{}"), Collections.emptyMap());
        hit.sourceRef(new BytesArray(source));

        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);

        //then
        assertEquals(new ResultModel("12345678", "LIMITED"), actual);
    }
}
