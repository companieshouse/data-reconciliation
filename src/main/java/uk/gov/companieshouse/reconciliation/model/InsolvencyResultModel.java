package uk.gov.companieshouse.reconciliation.model;

import java.util.Objects;

public class InsolvencyResultModel {

    private final String companyNumber;
    private final int insolvencyCases;

    public InsolvencyResultModel(String companyNumber, int insolvencyCases) {
        this.companyNumber = companyNumber;
        this.insolvencyCases = insolvencyCases;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public int getInsolvencyCases() {
        return insolvencyCases;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InsolvencyResultModel that = (InsolvencyResultModel) o;
        return insolvencyCases == that.insolvencyCases && Objects.equals(companyNumber, that.companyNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyNumber, insolvencyCases);
    }
}
