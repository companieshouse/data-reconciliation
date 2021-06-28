package uk.gov.companieshouse.reconciliation.function.compare_results.mapper;

import java.util.Collection;
import java.util.Map;

/**
 * Maps fields from a {@link Collection collection of objects} to a
 * {@link Map map of strings} representing pairings of IDs and required properties.
 */
public interface CompanyResultsMappable<T> {
    /**
     * Map ID and company properties of a {@link Collection collection of objects} to a {@link Map map of strings}.
     *
     * @param resultModels {@link Collection resultModels}
     * @return A {@link Map map of strings} mapped from the {@link Collection collection of objects}.
     */
    Map<String, String> generateMappings(Collection<T> resultModels);
}
