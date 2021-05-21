package uk.gov.companieshouse.reconciliation.service.elasticsearch;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.camel.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.Results;
import uk.gov.companieshouse.reconciliation.service.transformer.ResultsToCompanyNumberTransformer;

import java.util.Map;

@Component
public class ElasticsearchCompanyNumberTransformer {

    private ResultsToCompanyNumberTransformer resultsToCompanyNumberTransformer;

    @Autowired
    public ElasticsearchCompanyNumberTransformer(ResultsToCompanyNumberTransformer resultsToCompanyNumberTransformer) {
        this.resultsToCompanyNumberTransformer = resultsToCompanyNumberTransformer;
    }

    public void transform(@Body Results results, @Header("ElasticsearchDescription") String description,
                          @Header("ElasticsearchTargetHeader") String targetHeader, @Headers Map<String, Object> headers) {
        resultsToCompanyNumberTransformer.transform(results, description, targetHeader, headers);
    }
}
