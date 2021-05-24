package uk.gov.companieshouse.reconciliation.service.mongo;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.camel.Headers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.reconciliation.model.Results;
import uk.gov.companieshouse.reconciliation.service.transformer.ResultsToCompanyNumberTransformer;

import java.util.Map;

/**
 * Transform {@link Results company profile results} into a {@link uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList resource list}.
 */
@Component
public class MongoCompanyNumberTransformer {

    private final ResultsToCompanyNumberTransformer resultsToCompanyNumberTransformer;

    @Autowired
    public MongoCompanyNumberTransformer(ResultsToCompanyNumberTransformer resultsToCompanyNumberTransformer) {
        this.resultsToCompanyNumberTransformer = resultsToCompanyNumberTransformer;
    }

    /**
     * Transform {@link Results company profile results} into a {@link uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList resource list}.
     *
     * @param results A {@link Results results object} aggregating all company profiles returned from the collection.
     * @param description A description of the {@link uk.gov.companieshouse.reconciliation.function.compare_collection.entity.ResourceList resource list}.
     * @param targetHeader The header to which results will be stored.
     * @param headers {@link java.util.Map Name-value pairings} representing incoming headers.
     */
    public void transform(@Body Results results, @Header("MongoDescription") String description,
                          @Header("MongoTargetHeader") String targetHeader, @Headers Map<String, Object> headers) {
        resultsToCompanyNumberTransformer.transform(results, description, targetHeader, headers);
    }
}
