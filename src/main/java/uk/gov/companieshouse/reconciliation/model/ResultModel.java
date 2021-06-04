package uk.gov.companieshouse.reconciliation.model;

import java.util.Objects;

public class ResultModel {

    private final String companyNumber;
    private final String companyName;
    private final String companyStatus;

    public ResultModel(String companyNumber, String companyName) {
        this(companyNumber, companyName, "");
    }

    public ResultModel(String companyNumber, String companyName, String companyStatus) {
        this.companyNumber = companyNumber;
        this.companyName = companyName;
        this.companyStatus = companyStatus;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResultModel that = (ResultModel) o;
        return Objects.equals(companyNumber, that.companyNumber) && Objects
                .equals(companyName, that.companyName) && Objects
                .equals(companyStatus, that.companyStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyNumber, companyName, companyStatus);
    }
}
