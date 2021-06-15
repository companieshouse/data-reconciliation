package uk.gov.companieshouse.reconciliation.config;

public abstract class AbstractAggregationConfiguration {

    private String groupName;
    private int size;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
