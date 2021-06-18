package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.reconciliation.model.Results;
import uk.gov.companieshouse.reconciliation.service.transformer.ResultsToCompanyNumberTransformer;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ElasticsearchCompanyNumberTransformerTest {

    @InjectMocks
    private ElasticsearchCompanyNumberTransformer elasticsearchCompanyNumberTransformer;

    @Mock
    private ResultsToCompanyNumberTransformer resultsToCompanyNumberTransformer;

    @Mock
    private Results results;

    @Test
    void testResultsIsTransformedIntoResourceListUsingResultsTransformer() {
        // when
        elasticsearchCompanyNumberTransformer.transform(results, "description");

        // then
        verify(resultsToCompanyNumberTransformer).transform(results, "description");
    }

}
