package uk.gov.companieshouse.reconciliation.model;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class InsolvencyResults implements ResultAggregatable<InsolvencyResultModel>{

    private final Collection<InsolvencyResultModel> insolvencyResultModels;

    public InsolvencyResults(Collection<InsolvencyResultModel> insolvencyResultModels) {
        this.insolvencyResultModels = insolvencyResultModels;
    }

    public void add(InsolvencyResultModel insolvencyResultModel) {
        insolvencyResultModels.add(insolvencyResultModel);
    }

    public Collection<InsolvencyResultModel> getResultModels() {
        return Collections.unmodifiableCollection(insolvencyResultModels);
    }

    public boolean contains(InsolvencyResultModel insolvencyResultModel) {
        return insolvencyResultModels.contains(insolvencyResultModel);
    }

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
