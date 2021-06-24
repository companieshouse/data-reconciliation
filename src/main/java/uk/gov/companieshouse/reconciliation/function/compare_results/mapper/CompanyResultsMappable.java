package uk.gov.companieshouse.reconciliation.function.compare_results.mapper;

import uk.gov.companieshouse.reconciliation.model.ResultModel;

import java.util.Collection;
import java.util.Map;

/**
 * Maps fields from {@link Collection<ResultModel> resultModels} to a
 * {@link Map<String, String>} representing pairings of company ID and company properties.
 */
public interface CompanyResultsMappable {
    /**
     * Map ID and company properties of a {@link Collection<ResultModel>} to a {@link Map<String, String>}.
     *
     * @param resultModels {@link Collection<ResultModel> resultModels}
     * @return A {@link Map<String, String>} mapped from the {@link Collection<ResultModel> collection}.
     */
    Map<String, String> generateMappings(Collection<ResultModel> resultModels);
}
