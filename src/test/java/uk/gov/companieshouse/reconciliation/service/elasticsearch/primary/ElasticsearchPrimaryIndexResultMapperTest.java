package uk.gov.companieshouse.reconciliation.service.elasticsearch.primary;

import static org.junit.jupiter.api.Assertions.assertEquals;

import co.elastic.clients.elasticsearch.core.search.Hit;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
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
        Hit<Object> hit = Hit.of(b -> b.id("12345678").index("test-index").source(Map.of(
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
        Hit<Object> hit = Hit.of(b -> b.id("12345678").index("test-index").source(Map.of()));
        //when
        ResultModel actual = mapper.mapExcludingSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "", ""), actual);
    }

    @Test
    void testMapSearchHitReplaceNullValuesWithEmptyStrings() {
        //given
        Map<String, Object> item = new HashMap<>();
        item.put("corporate_name_start", null);
        item.put("corporate_name_ending", null);
        item.put("company_status", null);
        Map<String, Object> source = new HashMap<>();
        source.put("items", java.util.Collections.singletonList(item));
        Hit<Object> hit = Hit.of(b -> b.id("12345678").index("test-index").source(source));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "", ""), actual);
    }

    @Test
    void testMapSearchHitHandleEmptyItemsArray() {
        //given
        Hit<Object> hit = Hit.of(b -> b.id("12345678").index("test-index").source(Map.of(
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
        Map<String, Object> source = new HashMap<>();
        source.put("items", null);
        Hit<Object> hit = Hit.of(b -> b.id("12345678").index("test-index").source(source));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "", ""), actual);
    }

    @Test
    void testMapSearchHitNoSpaceBetweenNameStartAndNameEnding() {
        //given
        Hit<Object> hit = Hit.of(b -> b.id("12345678").index("test-index").source(Map.of(
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
        Hit<Object> hit = Hit.of(b -> b.id("12345678").index("test-index").source(Map.of(
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
        Hit<Object> hit = Hit.of(b -> b.id("12345678").index("test-index").source(Map.of(
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
        Hit<Object> hit = Hit.of(b -> b.id("12345678").index("test-index").source(Map.of(
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

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testMapSearchHitWithNullCorporateNameStart() {
        //given
        Map<String, Object> item = new HashMap<>();
        item.put("corporate_name_start", null);
        item.put("corporate_name_ending", " LIMITED");
        item.put("company_status", "active");
        Map<String, Object> source = new HashMap<>();
        source.put("items", java.util.Collections.singletonList(item));
        Hit<Object> hit = Hit.of(b -> b.id("12345678").index("test-index").source(source));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "LIMITED", "active"), actual);
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testMapSearchHitWithNullCorporateNameEnding() {
        //given
        Map<String, Object> item = new HashMap<>();
        item.put("corporate_name_start", "ACME");
        item.put("corporate_name_ending", null);
        item.put("company_status", "active");
        Map<String, Object> source = new HashMap<>();
        source.put("items", java.util.Collections.singletonList(item));
        Hit<Object> hit = Hit.of(b -> b.id("12345678").index("test-index").source(source));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "ACME", "active"), actual);
    }

    @SuppressWarnings("DataFlowIssue")
    @Test
    void testMapSearchHitWithNullCompanyStatus() {
        //given
        Map<String, Object> item = new HashMap<>();
        item.put("corporate_name_start", "ACME");
        item.put("corporate_name_ending", " LIMITED");
        item.put("company_status", null);
        Map<String, Object> source = new HashMap<>();
        source.put("items", java.util.Collections.singletonList(item));
        Hit<Object> hit = Hit.of(b -> b.id("12345678").index("test-index").source(source));
        //when
        ResultModel actual = mapper.mapWithSourceFields(hit);
        //then
        assertEquals(new ResultModel("12345678", "ACME LIMITED", ""), actual);
    }
}
