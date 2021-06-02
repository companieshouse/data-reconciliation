package uk.gov.companieshouse.reconciliation.function.compare_results.transformer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.reconciliation.model.Results;

@ExtendWith(MockitoExtension.class)
public class CompareCompanyStatusTransformerTest {

    @Mock
    private CompareFieldsResultsTransformer fieldsResultsTransformer;

    @Mock
    private Results sourceResults, targetResults;

    @InjectMocks
    private CompareCompanyStatusTransformer companyStatusTransformer;

    @Test
    void testThatResultOfTransformerIsReturnedCorrectly() {
        // Given
        List<Map<String, Object>> expected = Collections
                .singletonList(Collections.singletonMap("key", "value"));

        when(fieldsResultsTransformer.transform(any(), any(), any(), any(), any(), any()))
                .thenReturn(expected);

        // When
        List<Map<String, Object>> actual = companyStatusTransformer
                .transform(sourceResults, "apples", targetResults, "oranges", "fruit");

        // Then
        Assertions.assertEquals(expected, actual);

        verify(fieldsResultsTransformer, times(1))
                .transform(eq(sourceResults), eq("apples"), eq(targetResults), eq("oranges"),
                        eq("fruit"), any());
    }
}
