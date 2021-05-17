package uk.gov.companieshouse.reconciliation.function.compare_count.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CompareCountTransformerTest {

    private CompareCountTransformer transformer;

    @BeforeEach
    void setUp() {
        transformer = new CompareCountTransformer();
    }

    @Test
    void testCompareCounts() {
        //given
        ResourceList src = new ResourceList(Collections.singletonList("123"), "apples");
        ResourceList target = new ResourceList(Collections.singletonList("456"), "oranges");

        //when
        List<Map<String, Object>> actual = transformer.transform(src, target);

        //then
        assertEquals("apples", actual.get(0).get("src"));
        assertEquals("oranges", actual.get(0).get("target"));
        assertEquals("123", actual.get(1).get("src"));
        assertEquals("456", actual.get(1).get("target"));
    }
}
