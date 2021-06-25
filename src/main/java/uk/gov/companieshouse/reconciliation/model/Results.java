package uk.gov.companieshouse.reconciliation.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Aggregates details for a collection of companies.
 */
public class Results implements ResultAggregatable<ResultModel> {

    private final Collection<ResultModel> resultModels;

    public Results(Collection<ResultModel> resultModels) {
        this.resultModels = resultModels;
    }

    @Override
    public void add(ResultModel resultModel) {
        resultModels.add(resultModel);
    }

    @Override
    public Collection<ResultModel> getResultModels() {
        return Collections.unmodifiableCollection(resultModels);
    }

    @Override
    public boolean contains(ResultModel entry) {
        return resultModels.contains(entry);
    }

    @Override
    public int size() {
        return resultModels.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Results results = (Results) o;
        return Objects.equals(resultModels, results.resultModels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resultModels);
    }
}
