package uk.gov.companieshouse.reconciliation.model;

import java.util.List;

/**
 * Data used by the CHS notification API to render a template.
 */
public class EmailSendData {

    private final String to;
    private final String subject;
    private final String date;
    private final List<ResourceLink> resourceLinks;

    public EmailSendData(String to, String subject, String date, List<ResourceLink> resourceLinks) {
        this.to = to;
        this.subject = subject;
        this.date = date;
        this.resourceLinks = resourceLinks;
    }

    /**
     * @return The recipient(s) the email will be sent to.
     */
    public String getTo() {
        return to;
    }

    /**
     * @return The email's subject.
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @return The date results have been generated on.
     */
    public String getDate() {
        return date;
    }

    /**
     * @return Tuple of {@link ResourceLink links to results} and a description.
     */
    public List<ResourceLink> getResourceLinks() {
        return resourceLinks;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String to;
        private String subject;
        private String date;
        private List<ResourceLink> resourceLinkList;

        public Builder withTo(String to) {
            this.to = to;
            return this;
        }

        public Builder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder withDate(String date) {
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
