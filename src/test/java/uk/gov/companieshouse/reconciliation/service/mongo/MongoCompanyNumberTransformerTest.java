package uk.gov.companieshouse.reconciliation.service.mongo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.reconciliation.model.Results;
import uk.gov.companieshouse.reconciliation.service.transformer.ResultsToCompanyNumberTransformer;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MongoCompanyNumberTransformerTest {

    @InjectMocks
    private MongoCompanyNumberTransformer mongoCompanyNumberTransformer;

    @Mock
    private ResultsToCompanyNumberTransformer resultsToCompanyNumberTransformer;

    @Mock
    private Results results;

    @Test
    void testResultsIsTransformedIntoResourceListUsingResultsTransformer() {
        // when
        mongoCompanyNumberTransformer.transform(results, "description");

        // then
        verify(resultsToCompanyNumberTransformer).transform(results, "description");
    }

}
