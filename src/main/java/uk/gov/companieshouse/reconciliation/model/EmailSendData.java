package uk.gov.companieshouse.reconciliation.model;

import java.time.LocalDate;
import java.util.List;

public class EmailSendData {

    private String to;
    private String subject;
    private LocalDate date;
    private List<ResourceLink> resourceLinkList;

    public EmailSendData(String to, String subject, LocalDate date, List<ResourceLink> resourceLinkList) {
        this.to = to;
        this.subject = subject;
        this.date = date;
        this.resourceLinkList = resourceLinkList;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<ResourceLink> getResourceLinkList() {
        return resourceLinkList;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String to;
        private String subject;
        private LocalDate date;
        private List<ResourceLink> resourceLinkList;

        public Builder withTo(String to) {
            this.to = to;
            return this;
        }

        public Builder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder withDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder withResourceLinks(List<ResourceLink> resourceLinkList) {
            this.resourceLinkList = resourceLinkList;
            return this;
        }

        public EmailSendData build() {
            return new EmailSendData(to, subject, date, resourceLinkList);
        }
    }
}
