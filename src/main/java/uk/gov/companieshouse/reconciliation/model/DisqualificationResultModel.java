package uk.gov.companieshouse.reconciliation.model;

import java.util.Objects;

/**
 * Contains details about an individual disqualification.
 */
public class DisqualificationResultModel {

    private final String officerIdRaw;

    public DisqualificationResultModel(String officerIdRaw) {
        this.officerIdRaw = officerIdRaw;
    }

    /**
     * @return The ID of the disqualified officer as held in Oracle.
     */
    public String getOfficerIdRaw() {
        return officerIdRaw;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DisqualificationResultModel)) {
            return false;
        }
        DisqualificationResultModel that = (DisqualificationResultModel) o;
        return Objects.equals(getOfficerIdRaw(), that.getOfficerIdRaw());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOfficerIdRaw());
    }
}
