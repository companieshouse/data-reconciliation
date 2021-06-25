package uk.gov.companieshouse.reconciliation.model;

import java.util.Objects;

/**
 * Contains details about a single company.
 */
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

    /**
     * @return The company's company number.
     */
    public String getCompanyNumber() {
        return companyNumber;
    }

    /**
     * @return The company's name.
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * @return The company's status.
     */
    public String getCompanyStatus() {
        return companyStatus;
    }

    /**
     * @return A {@link Builder builder object} that can be used to construct details about a single company.
     */
    public static Builder builder() {
        return new Builder();
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

    /**
     * Construct an object containing details about a single company.
     */
    public static class Builder {

        private String companyNumber;
        private String companyName;
        private String companyStatus;

        private Builder() {
        }

        /**
         * Assign a company number to the company.
         *
         * @param companyNumber The company number belonging to the company.
         * @return The {@link Builder builder instance} being used to construct details about a single company.
         */
        public Builder withCompanyNumber(String companyNumber) {
            this.companyNumber = companyNumber;
            return this;
        }

        /**
         * Assign a name to the company.
         *
         * @param companyName The company's name.
         * @return The {@link Builder builder instance} being used to construct details about the company.
         */
        public Builder withCompanyName(String companyName) {
            this.companyName = companyName;
            return this;
        }

        /**
         * Assign a status to the company.
         *
         * @param companyStatus The company's status.
         * @return The {@link Builder builder instance} being used to construct details about the company.
         */
        public Builder withCompanyStatus(String companyStatus) {
            this.companyStatus = companyStatus;
            return this;
        }

        /**
         * Construct an {@link ResultModel object} containing details about the company.
         *
         * @return a {@link ResultModel object} containing details about the company.
         */
        public ResultModel build() {
            return new ResultModel(this);
        }
    }
}
