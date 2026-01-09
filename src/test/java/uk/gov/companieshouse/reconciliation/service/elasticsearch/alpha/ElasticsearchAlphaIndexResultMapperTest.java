package uk.gov.companieshouse.reconciliation.service.elasticsearch.alpha;

import static org.junit.jupiter.api.Assertions.assertEquals;

import co.elastic.clients.elasticsearch.core.search.Hit;
import java.util.Map;
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
        Hit<Object> hit = Hit.of(b -> b.id("12345678").source(Map.of(
            "items", Map.of(
                "corporate_name", "ACME LIMITED",
                "company_status", "active"
            )
        )));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "ACME LIMITED", "active"), actual);
    }

    @Test
    void testMapSearchHitWithoutSourceFields() {
        //given
        Hit<Object> hit = Hit.of(b -> b.id("12345678").source(null));
        //when
        ResultModel actual = mapper.mapExcludingSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", ""), actual);
    }

    @Test
    void testMapSearchHitReplaceNullValuesWithEmptyStrings() {
        //given
        Hit<Object> hit = Hit.of(b -> b.id("12345678").source(Map.of(
            "items", Map.of(
                "corporate_name", null,
                "company_status", null
            )
        )));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "", ""), actual);
    }

    @Test
    void testMapSearchHitHandleEmptyItemsObject() {
        //given
        Hit<Object> hit = Hit.of(b -> b.id("12345678").source(Map.of(
            "items", Map.of()
        )));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "", ""), actual);
    }

    @Test
    void testMapSearchHitHandleNullItems() {
        //given
        Hit<Object> hit = Hit.of(b -> b.id("12345678").source(Map.of(
            "items", null
        )));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "", ""), actual);
    }

    @Test
    void testMapSearchHitTrimSourceFields() {
        //given
        Hit<Object> hit = Hit.of(b -> b.id("12345678").source(Map.of(
            "items", Map.of(
                "corporate_name", "   ACME LIMITED ",
                "company_status", "  active   "
            )
        )));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "ACME LIMITED", "active"), actual);
    }
}
