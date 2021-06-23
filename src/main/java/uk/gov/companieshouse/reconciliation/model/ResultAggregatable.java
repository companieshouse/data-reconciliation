package uk.gov.companieshouse.reconciliation.model;

import java.util.Collection;

/**
 * Aggregates a collection of results fetched and mapped from a data source.
 *
 * @param <T> The data type of the objects that will be aggregated.
 */
public interface ResultAggregatable<T> {

    /**
     * Add a result to the collection of results.
     *
     * @param resultModel The result that will be added.
     */
    void add(T resultModel);

    /**
     * @return A {@link Collection collection} of results that have been fetched and mapped from a data source.
     */
    Collection<T> getResultModels();

    /**
     * Determines if the specified result exists within the collection of results.
     *
     * @param resultModel The result that will be checked for existence.
     * @return true if the result exists in the collection, otherwise false.
     */
    boolean contains(T resultModel);

    /**
     * @return The size of the collection of results.
     */
    int size();
}
