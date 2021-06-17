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

    private ResultModel(Builder builder) {
        companyNumber = builder.companyNumber;
        companyName = builder.companyName;
        companyStatus = builder.companyStatus;
    }

    public static Builder builder() {
        return new Builder();
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

    public static class Builder {

        private String companyNumber;
        private String companyName;
        private String companyStatus;

        private Builder() {
        }

        public Builder withCompanyNumber(String companyNumber) {
            this.companyNumber = companyNumber;
            return this;
        }

        public Builder withCompanyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        public Builder withCompanyStatus(String companyStatus) {
            this.companyStatus = companyStatus;
            return this;
        }

        public ResultModel build() {
            return new ResultModel(this);
        }
    }
}
