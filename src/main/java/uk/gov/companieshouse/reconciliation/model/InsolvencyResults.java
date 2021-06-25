package uk.gov.companieshouse.reconciliation.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Aggregates insolvency details for companies.
 */
public class InsolvencyResults implements ResultAggregatable<InsolvencyResultModel>{

    private final Collection<InsolvencyResultModel> insolvencyResultModels;

    public InsolvencyResults(Collection<InsolvencyResultModel> insolvencyResultModels) {
        this.insolvencyResultModels = insolvencyResultModels;
    }

    @Override
    public void add(InsolvencyResultModel insolvencyResultModel) {
        insolvencyResultModels.add(insolvencyResultModel);
    }

    @Override
    public Collection<InsolvencyResultModel> getResultModels() {
        return Collections.unmodifiableCollection(insolvencyResultModels);
    }

    @Override
    public boolean contains(InsolvencyResultModel insolvencyResultModel) {
        return insolvencyResultModels.contains(insolvencyResultModel);
    }

    @Override
    public int size() {
        return insolvencyResultModels.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InsolvencyResults that = (InsolvencyResults) o;
        return Objects.equals(insolvencyResultModels, that.insolvencyResultModels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(insolvencyResultModels);
    }
}
