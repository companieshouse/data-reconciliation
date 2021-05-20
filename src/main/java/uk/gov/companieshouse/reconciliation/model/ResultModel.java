package uk.gov.companieshouse.reconciliation.model;

import java.util.Objects;

public class ResultModel {

    private final String companyNumber;
    private final String companyName;

    public ResultModel(String companyNumber, String companyName) {
        this.companyNumber = companyNumber;
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResultModel)) return false;
        ResultModel that = (ResultModel) o;
        return Objects.equals(getCompanyNumber(), that.getCompanyNumber()) &&
                Objects.equals(getCompanyName(), that.getCompanyName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCompanyNumber(), getCompanyName());
    }
}
