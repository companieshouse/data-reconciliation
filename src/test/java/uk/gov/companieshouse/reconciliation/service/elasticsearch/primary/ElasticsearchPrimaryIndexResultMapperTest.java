package uk.gov.companieshouse.reconciliation.service.elasticsearch.primary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.search.SearchHit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.gov.companieshouse.reconciliation.model.ResultModel;

public class ElasticsearchPrimaryIndexResultMapperTest {

    private ElasticsearchPrimaryIndexResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ElasticsearchPrimaryIndexResultMapper();
    }

    @Test
    void testMapSearchHitIntoResultModel() {
        //given
        String source = "{ \"items\": [{\"corporate_name_start\": \"ACME\", \"corporate_name_ending\": \" LIMITED\", \"company_status\": \"active\"}] }";
        SearchHit hit = mock(SearchHit.class);
        when(hit.getId()).thenReturn("12345678");
        when(hit.getSourceRef()).thenReturn(new BytesArray(source));
        when(hit.getSourceAsMap()).thenReturn(Map.of(
                "items", java.util.List.of(
                        Map.of(
                                "corporate_name_start", "ACME",
                                "corporate_name_ending", " LIMITED",
                                "company_status", "active"
                        )
                )
        ));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "ACME LIMITED", "active"), actual);
    }

    @Test
    void testMapSearchHitWithoutSourceFields() {
        //given
        SearchHit hit = mock(SearchHit.class);
        when(hit.getId()).thenReturn("12345678");
        when(hit.getSourceAsMap()).thenReturn(Map.of());
        //when
        ResultModel actual = mapper.mapExcludingSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "", ""), actual);
    }

    @Test
    void testMapSearchHitReplaceNullValuesWithEmptyStrings() {
        //given
        String source = "{ \"items\": [{\"corporate_name_start\": null, \"corporate_name_ending\": null, \"company_status\": null}] }";
        SearchHit hit = mock(SearchHit.class);
        when(hit.getId()).thenReturn("12345678");
        when(hit.getSourceRef()).thenReturn(new BytesArray(source));
        java.util.Map<String, Object> item = new java.util.HashMap<>();
        item.put("corporate_name_start", null);
        item.put("corporate_name_ending", null);
        item.put("company_status", null);
        when(hit.getSourceAsMap()).thenReturn(Map.of(
                "items", java.util.List.of(item)
        ));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "", ""), actual);
    }

    @Test
    void testMapSearchHitHandleEmptyItemsArray() {
        //given
        String source = "{ \"items\": [] }";
        SearchHit hit = mock(SearchHit.class);
        when(hit.getId()).thenReturn("12345678");
        when(hit.getSourceRef()).thenReturn(new BytesArray(source));
        when(hit.getSourceAsMap()).thenReturn(Map.of(
                "items", java.util.List.of()
        ));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "", ""), actual);
    }

    @Test
    void testMapSearchHitHandleNullItems() {
        //given
        String source = "{ \"items\": null }";
        SearchHit hit = mock(SearchHit.class);
        when(hit.getId()).thenReturn("12345678");
        when(hit.getSourceRef()).thenReturn(new BytesArray(source));
        java.util.Map<String, Object> mapWithNull = new java.util.HashMap<>();
        mapWithNull.put("items", null);
        when(hit.getSourceAsMap()).thenReturn(mapWithNull);
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "", ""), actual);
    }

    @Test
    void testMapSearchHitNoSpaceBetweenNameStartAndNameEnding() {
        //given
        String source = "{ \"items\": [{\"corporate_name_start\": \"ACME\", \"corporate_name_ending\": \"LIMITED\", \"company_status\": \"active\"}] }";
        SearchHit hit = mock(SearchHit.class);
        when(hit.getId()).thenReturn("12345678");
        when(hit.getSourceRef()).thenReturn(new BytesArray(source));
        when(hit.getSourceAsMap()).thenReturn(Map.of(
                "items", java.util.List.of(
                        Map.of(
                                "corporate_name_start", "ACME",
                                "corporate_name_ending", "LIMITED",
                                "company_status", "active"
                        )
                )
        ));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "ACME LIMITED", "active"), actual);
    }

    @Test
    void testMapSearchHitNameEndingAbsent() {
        //given
        String source = "{ \"items\": [{\"corporate_name_start\": \"ACME\", \"corporate_name_ending\": \"\", \"company_status\": \"active\"}] }";
        SearchHit hit = mock(SearchHit.class);
        when(hit.getId()).thenReturn("12345678");
        when(hit.getSourceRef()).thenReturn(new BytesArray(source));
        java.util.Map<String, Object> item = new java.util.HashMap<>();
        item.put("corporate_name_start", "ACME");
        item.put("corporate_name_ending", "");
        item.put("company_status", "active");
        when(hit.getSourceAsMap()).thenReturn(Map.of(
                "items", java.util.List.of(item)
        ));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "ACME", "active"), actual);
    }

    @Test
    void testMapSearchHitNameStartAbsent() {
        //given
        String source = "{ \"items\": [{\"corporate_name_start\": \"\", \"corporate_name_ending\": \" LIMITED\", \"company_status\": \"active\"}] }";
        SearchHit hit = mock(SearchHit.class);
        when(hit.getId()).thenReturn("12345678");
        when(hit.getSourceRef()).thenReturn(new BytesArray(source));
        when(hit.getSourceAsMap()).thenReturn(Map.of(
                "items", java.util.List.of(
                        Map.of(
                                "corporate_name_start", "",
                                "corporate_name_ending", " LIMITED",
                                "company_status", "active"
                        )
                )
        ));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "LIMITED", "active"), actual);
    }

    @Test
    void testMapSearchHitWithWhitespaceOnCompanyStatus() {
        //given
        String source = "{ \"items\": [{\"corporate_name_start\": \"\", \"corporate_name_ending\": \" LIMITED\", \"company_status\": \" active \"}] }";
        SearchHit hit = mock(SearchHit.class);
        when(hit.getId()).thenReturn("12345678");
        when(hit.getSourceRef()).thenReturn(new BytesArray(source));
        java.util.Map<String, Object> item = new java.util.HashMap<>();
        item.put("corporate_name_start", "");
        item.put("corporate_name_ending", " LIMITED");
        item.put("company_status", " active ");
        when(hit.getSourceAsMap()).thenReturn(Map.of(
                "items", java.util.List.of(item)
        ));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "LIMITED", "active"), actual);
    }
}
