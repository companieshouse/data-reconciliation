package uk.gov.companieshouse.reconciliation.service.elasticsearch.primary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import co.elastic.clients.elasticsearch.core.search.Hit;
import java.util.Map;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.model.ResultModel;

class ElasticsearchPrimaryIndexResultMapperTest {
    private ElasticsearchPrimaryIndexResultMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ElasticsearchPrimaryIndexResultMapper();
    }

    @Test
    void testMapSearchHitIntoResultModel() {
        //given
        Hit<Object> hit = Hit.of(b -> b.id("12345678").source(Map.of(
            "items", List.of(Map.of(
                "corporate_name_start", "ACME",
                "corporate_name_ending", " LIMITED",
                "company_status", "active"
            ))
        )));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "ACME LIMITED", "active"), actual);
    }

    @Test
    void testMapSearchHitWithoutSourceFields() {
        //given
        Hit<Object> hit = Hit.of(b -> b.id("12345678").source(Map.of()));
        //when
        ResultModel actual = mapper.mapExcludingSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "", ""), actual);
    }

    @Test
    void testMapSearchHitReplaceNullValuesWithEmptyStrings() {
        //given
        Hit<Object> hit = Hit.of(b -> b.id("12345678").source(Map.of(
            "items", List.of(Map.of(
                "corporate_name_start", null,
                "corporate_name_ending", null,
                "company_status", null
            ))
        )));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "", ""), actual);
    }

    @Test
    void testMapSearchHitHandleEmptyItemsArray() {
        //given
        Hit<Object> hit = Hit.of(b -> b.id("12345678").source(Map.of(
            "items", List.of()
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
    void testMapSearchHitNoSpaceBetweenNameStartAndNameEnding() {
        //given
        Hit<Object> hit = Hit.of(b -> b.id("12345678").source(Map.of(
            "items", List.of(Map.of(
                "corporate_name_start", "ACME",
                "corporate_name_ending", "LIMITED",
                "company_status", "active"
            ))
        )));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "ACME LIMITED", "active"), actual);
    }

    @Test
    void testMapSearchHitNameEndingAbsent() {
        //given
        Hit<Object> hit = Hit.of(b -> b.id("12345678").source(Map.of(
            "items", List.of(Map.of(
                "corporate_name_start", "ACME",
                "corporate_name_ending", "",
                "company_status", "active"
            ))
        )));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "ACME", "active"), actual);
    }

    @Test
    void testMapSearchHitNameStartAbsent() {
        //given
        Hit<Object> hit = Hit.of(b -> b.id("12345678").source(Map.of(
            "items", List.of(Map.of(
                "corporate_name_start", "",
                "corporate_name_ending", " LIMITED",
                "company_status", "active"
            ))
        )));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "LIMITED", "active"), actual);
    }

    @Test
    void testMapSearchHitWithWhitespaceOnCompanyStatus() {
        //given
        Hit<Object> hit = Hit.of(b -> b.id("12345678").source(Map.of(
            "items", List.of(Map.of(
                "corporate_name_start", "",
                "corporate_name_ending", " LIMITED",
                "company_status", " active "
            ))
        )));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "LIMITED", "active"), actual);
    }
}
