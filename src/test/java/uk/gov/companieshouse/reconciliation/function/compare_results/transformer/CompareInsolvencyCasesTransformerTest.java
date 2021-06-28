package uk.gov.companieshouse.reconciliation.function.compare_results.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.reconciliation.function.compare_results.mapper.CompareInsolvencyCaseCountsResultMapper;
import uk.gov.companieshouse.reconciliation.model.InsolvencyResults;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertSame;

@ExtendWith(MockitoExtension.class)
public class CompareInsolvencyCasesTransformerTest {

    private CompareInsolvencyCasesTransformer transformer;

    @Mock
    private CompareFieldsResultsTransformer fieldsResultsTransformer;

    @Mock
    private CompareInsolvencyCaseCountsResultMapper mapper;

    @Mock
    private InsolvencyResults src, target;

    @BeforeEach
    void setUp() {
        transformer = new CompareInsolvencyCasesTransformer(fieldsResultsTransformer, mapper);
    }

    @Test
    void testInsolvencyCasesTransformerDelegatesToCompareFieldsResultsTransformer() {
        //given
        List<Map<String, Object>> expected = Collections.singletonList(Collections.singletonMap("key", "key"));
        when(fieldsResultsTransformer.transform(any(), any(), any(), any(), any(), any())).thenReturn(expected);

        //when
        List<Map<String, Object>> actual = transformer.transform(src, "Src", target, "Target", "key");

        //then
        assertSame(expected, actual);
        verify(fieldsResultsTransformer).transform(src, "Src", target, "Target", "key", mapper);
    }

}