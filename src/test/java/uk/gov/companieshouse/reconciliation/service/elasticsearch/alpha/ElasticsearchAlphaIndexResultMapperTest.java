package uk.gov.companieshouse.reconciliation.service.elasticsearch.alpha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.search.SearchHit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.gov.companieshouse.reconciliation.model.ResultModel;

class ElasticsearchAlphaIndexResultMapperTest {

    private ElasticsearchAlphaIndexResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ElasticsearchAlphaIndexResultMapper();
    }

    @Test
    void testMapSearchHitToResultModel() {
        //given
        String source = "{ \"items\": {\"corporate_name\": \"ACME LIMITED\", \"company_status\": \"active\"} }";
        SearchHit hit = mock(SearchHit.class);
        when(hit.getId()).thenReturn("12345678");
        when(hit.getSourceRef()).thenReturn(new BytesArray(source));
        // The mapper expects getSourceAsMap() to return a Map with an 'items' key
        when(hit.getSourceAsMap()).thenReturn(Map.of(
            "items", Map.of(
                "corporate_name", "ACME LIMITED",
                "company_status", "active"
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
        when(hit.getSourceRef()).thenReturn(null);

        //when
        ResultModel actual = mapper.mapExcludingSourceFields(hit);

        //then
        assertEquals(new ResultModel("12345678", "", ""), actual);
    }

    @Test
    void testMapSearchHitReplaceNullValuesWithEmptyStrings() {
        //given
        String source = "{ \"items\": {\"corporate_name\": null, \"company_status\": null} }";
        SearchHit hit = mock(SearchHit.class);
        when(hit.getId()).thenReturn("12345678");
        when(hit.getSourceRef()).thenReturn(new BytesArray(source));

        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);

        //then
        assertEquals(new ResultModel("12345678", "", ""), actual);
    }

    @Test
    void testMapSearchHitHandleEmptyItemsObject() {
        //given
        String source = "{ \"items\": {} }";
        SearchHit hit = mock(SearchHit.class);
        when(hit.getId()).thenReturn("12345678");
        when(hit.getSourceRef()).thenReturn(new BytesArray(source));

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

        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);

        //then
        assertEquals(new ResultModel("12345678", "", ""), actual);
    }

    @Test
    void testMapSearchHitTrimSourceFields() {
        //given
        String source = "{ \"items\": {\"corporate_name\": \"   ACME LIMITED \", \"company_status\": \"  active   \"} }";
        SearchHit hit = mock(SearchHit.class);
        when(hit.getId()).thenReturn("12345678");
        when(hit.getSourceRef()).thenReturn(new BytesArray(source));
        when(hit.getSourceAsMap()).thenReturn(Map.of(
            "items", Map.of(
                "corporate_name", "   ACME LIMITED ",
                "company_status", "  active   "
            )
        ));

        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);

        //then
        assertEquals(new ResultModel("12345678", "ACME LIMITED", "active"), actual);
    }
}
