package uk.gov.companieshouse.reconciliation.model;

import java.util.Objects;

/**
 * Contains insolvency details for a single company.
 */
public class InsolvencyResultModel {

    private final String companyNumber;
    private final int insolvencyCases;

    public InsolvencyResultModel(String companyNumber, int insolvencyCases) {
        this.companyNumber = companyNumber;
        this.insolvencyCases = insolvencyCases;
    }

    /**
     * @return The company number against which insolvency details are held.
     */
    public String getCompanyNumber() {
        return companyNumber;
    }

    /**
     * @return The number of insolvency cases against the company.
     */
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
