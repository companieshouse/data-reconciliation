package uk.gov.companieshouse.reconciliation.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Aggregates a collection of disqualification items.
 */
public class DisqualificationResults implements ResultAggregatable<DisqualificationResultModel> {

    private final Collection<DisqualificationResultModel> disqualifications;

    public DisqualificationResults(Collection<DisqualificationResultModel> disqualifications) {
        this.disqualifications = disqualifications;
    }

    @Override
    public void add(DisqualificationResultModel resultModel) {
        disqualifications.add(resultModel);
    }

    @Override
    public Collection<DisqualificationResultModel> getResultModels() {
        return Collections.unmodifiableCollection(disqualifications);
    }

    @Override
    public boolean contains(DisqualificationResultModel resultModel) {
        return disqualifications.contains(resultModel);
    }

    @Override
    public int size() {
        return disqualifications.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DisqualificationResults)) {
            return false;
        }
        DisqualificationResults that = (DisqualificationResults) o;
        return Objects.equals(disqualifications, that.disqualifications);
    }

    @Override
    public int hashCode() {
        return Objects.hash(disqualifications);
    }
}
