package uk.gov.companieshouse.reconciliation.model;

public class EmailSendModel {

    private String applicationId;
    private String messageId;
    private String messageType;
    private EmailSendData emailSendData;
    private String emailAddress;

    public EmailSendModel(String applicationId, String messageId, String messageType, EmailSendData emailSendData, String emailAddress) {
        this.applicationId = applicationId;
        this.messageId = messageId;
        this.messageType = messageType;
        this.emailSendData = emailSendData;
        this.emailAddress = emailAddress;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public EmailSendData getEmailSendData() {
        return emailSendData;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {

        private String applicationId;
        private String messageId;
        private String messageType;
        private EmailSendData emailSendData;
        private String emailAddress;

        public Builder withApplicationId(String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public Builder withMessageId(String messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder withMessageType(String messageType) {
            this.messageType = messageType;
            return this;
        }

        public Builder withEmailSendData(EmailSendData emailSendData) {
            this.emailSendData = emailSendData;
            return this;
        }

        public Builder withEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public EmailSendModel build() {
            return new EmailSendModel(applicationId, messageId, messageType, emailSendData, emailAddress);
        }
    }

}
