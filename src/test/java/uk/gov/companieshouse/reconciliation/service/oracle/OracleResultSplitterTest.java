package uk.gov.companieshouse.reconciliation.service.oracle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OracleResultSplitterTest {

    private OracleResultSplitter splitter;

    @BeforeEach
    void setUp() {
        this.splitter = new OracleResultSplitter();
    }

    @Test
    void testFilterNullValuesFromResults() {
        //given
        List<Map<String, Object>> results = Collections.singletonList(Collections.singletonMap("RESULT", null));

        //when
        Iterator<String> actual = splitter.split(results);

        //then
        assertFalse(actual.hasNext());
    }

    @Test
    void testReturnStringRepresentationOfValues() {
        //given
        List<Map<String, Object>> results = Collections.singletonList(Collections.singletonMap("RESULT", 42));

        //when
        Iterator<String> actual = splitter.split(results);

        //then
        assertTrue(actual.hasNext());
        assertEquals("42", actual.next());
    }
}
