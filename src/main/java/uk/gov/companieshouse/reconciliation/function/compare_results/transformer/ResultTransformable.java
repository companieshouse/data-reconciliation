package uk.gov.companieshouse.reconciliation.function.compare_results.transformer;

import uk.gov.companieshouse.reconciliation.model.ResultAggregatable;

import java.util.List;
import java.util.Map;

/**
 * Compare two {@link ResultAggregatable results objects} and record any differences for each result with a
 * matching ID.
 */
public interface ResultTransformable<T> {

    /**
     * Compare two {@link ResultAggregatable results objects} and record any differences for each result with a
     * matching ID.
     *
     * @param srcResults        A {@link ResultAggregatable results object} containing data retrieved from one
     *                          endpoint.
     * @param srcDescription    A description of the first endpoint from which {@link ResultAggregatable
     *                          results} have been retrieved.
     * @param targetResults     A {@link ResultAggregatable results object} containing data retrieved from
     *                          another endpoint.
     * @param targetDescription A description of the second endpoint from which {@link ResultAggregatable
     *                          results} have been retrieved.
     * @param recordKey         A description of the type of data being compared.
     * @return A {@link List list} containing {@link Map company number and data name-value
     * pairings}.
     */
    List<Map<String, Object>> transform(T srcResults,
                                               String srcDescription,
                                               T targetResults,
                                               String targetDescription,
                                               String recordKey);
}
