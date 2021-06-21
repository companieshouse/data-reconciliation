package uk.gov.companieshouse.reconciliation.config;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class ComparisonGroupModel {

    @NotEmpty
    private String groupName;

    @NotNull
    private Map<String, EmailLinkModel> emailLinkModel;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getSize() {
        return emailLinkModel.size();
    }

    public Map<String, EmailLinkModel> getEmailLinkModel() {
        return emailLinkModel;
    }

    public void setEmailLinkModel(Map<String, EmailLinkModel> emailLinkModel) {
        this.emailLinkModel = emailLinkModel;
    }
}
